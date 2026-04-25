package dev.havlicektomas.photosapp.core.navigation

import dev.havlicektomas.photosapp.feature.home.domain.Photo
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data class DetailRoute(
    val id: String,
    val title: String,
    val imageUrl: String,
    val tags: List<String>,
)

fun Photo.toDetailRoute(): DetailRoute = DetailRoute(
    id = id,
    title = title,
    imageUrl = imageUrl,
    tags = tags,
)

fun DetailRoute.toPhoto(): Photo = Photo(
    id = id,
    title = title,
    imageUrl = imageUrl,
    tags = tags,
)
