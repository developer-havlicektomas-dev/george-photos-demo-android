package dev.havlicektomas.photosapp.feature.home.data

import dev.havlicektomas.photosapp.feature.home.domain.Photo

fun FlickrFeedDto.toPhotos(): List<Photo> = items.map { it.toPhoto() }

fun FlickrItemDto.toPhoto(): Photo = Photo(
    id = link,
    title = title,
    imageUrl = media.m,
    tags = tags.split(' ').filter { it.isNotBlank() },
)
