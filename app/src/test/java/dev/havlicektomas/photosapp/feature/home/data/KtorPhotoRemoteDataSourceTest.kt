package dev.havlicektomas.photosapp.feature.home.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.havlicektomas.photosapp.core.domain.util.DataError
import dev.havlicektomas.photosapp.core.domain.util.Result
import dev.havlicektomas.photosapp.core.network.HttpClientFactory
import dev.havlicektomas.photosapp.feature.home.domain.Photo
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class KtorPhotoRemoteDataSourceTest {

    @Test
    fun `fetchPhotos returns mapped photos on 200`() = runTest {
        val dataSource = createDataSource {
            respondJson(SAMPLE_FEED_JSON)
        }

        val result = dataSource.fetchPhotos()

        assertThat(result).isEqualTo(
            Result.Success(
                listOf(
                    Photo(
                        id = "https://flickr.com/p/1",
                        title = "First",
                        imageUrl = "https://live.staticflickr.com/1.jpg",
                        tags = listOf("alpha", "beta"),
                    ),
                    Photo(
                        id = "https://flickr.com/p/2",
                        title = "Second",
                        imageUrl = "https://live.staticflickr.com/2.jpg",
                        tags = emptyList(),
                    ),
                ),
            ),
        )
    }

    @Test
    fun `fetchPhotos hits the public photos feed with format and nojsoncallback`() = runTest {
        var captured: HttpRequestData? = null
        val dataSource = createDataSource { request ->
            captured = request
            respondJson("""{"items":[]}""")
        }

        dataSource.fetchPhotos()

        val url = captured!!.url
        assertThat(url.host).isEqualTo("api.flickr.com")
        assertThat(url.encodedPath).isEqualTo("/services/feeds/photos_public.gne")
        assertThat(url.parameters["format"]).isEqualTo("json")
        assertThat(url.parameters["nojsoncallback"]).isEqualTo("1")
    }

    @Test
    fun `fetchPhotos returns SERIALIZATION when 200 body is malformed`() = runTest {
        val dataSource = createDataSource {
            respondJson("not really json")
        }

        val result = dataSource.fetchPhotos()

        assertThat(result).isEqualTo(Result.Error(DataError.Network.SERIALIZATION))
    }

    @Test
    fun `fetchPhotos returns NO_INTERNET when engine throws UnresolvedAddressException`() = runTest {
        val dataSource = createDataSource {
            throw UnresolvedAddressException()
        }

        val result = dataSource.fetchPhotos()

        assertThat(result).isEqualTo(Result.Error(DataError.Network.NO_INTERNET))
    }

    @ParameterizedTest
    @MethodSource("statusCodeMapping")
    fun `fetchPhotos maps HTTP status to DataError Network`(
        status: Int,
        expected: DataError.Network,
    ) = runTest {
        val dataSource = createDataSource {
            respond(content = "", status = HttpStatusCode.fromValue(status))
        }

        val result = dataSource.fetchPhotos()

        assertThat(result).isEqualTo(Result.Error(expected))
    }

    private fun createDataSource(
        handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData,
    ): KtorPhotoRemoteDataSource {
        val engine = MockEngine(handler)
        val client = HttpClientFactory.create(engine)
        return KtorPhotoRemoteDataSource(client)
    }

    private fun MockRequestHandleScope.respondJson(content: String): HttpResponseData {
        return respond(
            content = content,
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
        )
    }

    companion object {
        @JvmStatic
        fun statusCodeMapping(): Stream<Arguments> = Stream.of(
            Arguments.of(401, DataError.Network.UNAUTHORIZED),
            Arguments.of(408, DataError.Network.REQUEST_TIMEOUT),
            Arguments.of(409, DataError.Network.CONFLICT),
            Arguments.of(413, DataError.Network.PAYLOAD_TOO_LARGE),
            Arguments.of(429, DataError.Network.TOO_MANY_REQUESTS),
            Arguments.of(500, DataError.Network.SERVER_ERROR),
            Arguments.of(418, DataError.Network.UNKNOWN),
        )

        private val SAMPLE_FEED_JSON = """
            {
              "items": [
                {
                  "title": "First",
                  "link": "https://flickr.com/p/1",
                  "media": { "m": "https://live.staticflickr.com/1.jpg" },
                  "tags": "alpha beta"
                },
                {
                  "title": "Second",
                  "link": "https://flickr.com/p/2",
                  "media": { "m": "https://live.staticflickr.com/2.jpg" },
                  "tags": ""
                }
              ]
            }
        """.trimIndent()
    }
}
