package dev.havlicektomas.photosapp.feature.home.domain

import dev.havlicektomas.photosapp.core.domain.util.DataError
import dev.havlicektomas.photosapp.core.domain.util.Result

interface PhotoRemoteDataSource {
    suspend fun fetchPhotos(tags: List<String> = emptyList()): Result<List<Photo>, DataError.Network>
}
