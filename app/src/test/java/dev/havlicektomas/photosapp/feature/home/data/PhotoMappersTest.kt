package dev.havlicektomas.photosapp.feature.home.data

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import dev.havlicektomas.photosapp.feature.home.domain.Photo
import org.junit.jupiter.api.Test

class PhotoMappersTest {

    @Test
    fun `toPhoto maps id link title and imageUrl`() {
        val dto = FlickrItemDto(
            title = "Sunset",
            link = "https://flickr.com/p/123",
            media = FlickrMediaDto(m = "https://live.staticflickr.com/img.jpg"),
            tags = "sunset beach",
        )

        val photo = dto.toPhoto()

        assertThat(photo).isEqualTo(
            Photo(
                id = "https://flickr.com/p/123",
                title = "Sunset",
                imageUrl = "https://live.staticflickr.com/img.jpg",
                tags = listOf("sunset", "beach"),
            ),
        )
    }

    @Test
    fun `toPhoto splits tags on single space`() {
        val dto = FlickrItemDto(tags = "one two three")

        assertThat(dto.toPhoto().tags).isEqualTo(listOf("one", "two", "three"))
    }

    @Test
    fun `toPhoto returns empty list when tags string is empty`() {
        val dto = FlickrItemDto(tags = "")

        assertThat(dto.toPhoto().tags).isEmpty()
    }

    @Test
    fun `toPhoto filters blanks from consecutive or surrounding spaces`() {
        val dto = FlickrItemDto(tags = "  alpha   beta  ")

        assertThat(dto.toPhoto().tags).isEqualTo(listOf("alpha", "beta"))
    }

    @Test
    fun `toPhotos preserves item order`() {
        val feed = FlickrFeedDto(
            items = listOf(
                FlickrItemDto(link = "1"),
                FlickrItemDto(link = "2"),
                FlickrItemDto(link = "3"),
            ),
        )

        assertThat(feed.toPhotos().map { it.id }).isEqualTo(listOf("1", "2", "3"))
    }

    @Test
    fun `toPhotos returns empty list when items are empty`() {
        assertThat(FlickrFeedDto(items = emptyList()).toPhotos()).isEmpty()
    }
}
