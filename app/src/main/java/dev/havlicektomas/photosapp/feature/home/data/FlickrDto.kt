package dev.havlicektomas.photosapp.feature.home.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FlickrFeedDto(
    @SerialName("items") val items: List<FlickrItemDto> = emptyList(),
)

@Serializable
data class FlickrItemDto(
    @SerialName("title") val title: String = "",
    @SerialName("link") val link: String = "",
    @SerialName("media") val media: FlickrMediaDto = FlickrMediaDto(),
    @SerialName("tags") val tags: String = "",
)

@Serializable
data class FlickrMediaDto(
    @SerialName("m") val m: String = "",
)
