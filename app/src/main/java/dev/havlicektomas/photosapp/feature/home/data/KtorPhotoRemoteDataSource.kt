package dev.havlicektomas.photosapp.feature.home.data

import dev.havlicektomas.photosapp.core.domain.util.DataError
import dev.havlicektomas.photosapp.core.domain.util.Result
import dev.havlicektomas.photosapp.core.domain.util.map
import dev.havlicektomas.photosapp.core.network.HttpRoutes
import dev.havlicektomas.photosapp.core.network.get
import dev.havlicektomas.photosapp.feature.home.domain.Photo
import dev.havlicektomas.photosapp.feature.home.domain.PhotoRemoteDataSource
import io.ktor.client.HttpClient

class KtorPhotoRemoteDataSource(
    private val httpClient: HttpClient,
) : PhotoRemoteDataSource {
    override suspend fun fetchPhotos(): Result<List<Photo>, DataError.Network> {
        return httpClient.get<FlickrFeedDto>(
            route = HttpRoutes.PUBLIC_PHOTOS_FEED,
            queryParameters = mapOf(
                "format" to "json",
                "nojsoncallback" to 1,
            ),
        ).map { it.toPhotos() }
    }
}
