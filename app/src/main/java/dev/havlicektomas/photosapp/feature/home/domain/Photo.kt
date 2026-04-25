package dev.havlicektomas.photosapp.feature.home.domain

data class Photo(
    val id: String,
    val title: String,
    val imageUrl: String,
    val tags: List<String>,
)
