package dev.havlicektomas.photosapp.feature.home.presentation

import dev.havlicektomas.photosapp.core.domain.util.DataError
import dev.havlicektomas.photosapp.core.domain.util.Result
import dev.havlicektomas.photosapp.feature.home.domain.Photo
import dev.havlicektomas.photosapp.feature.home.domain.PhotoRemoteDataSource
import kotlinx.coroutines.CompletableDeferred

class FakePhotoRemoteDataSource(
    initialResponse: Result<List<Photo>, DataError.Network> = Result.Success(emptyList()),
) : PhotoRemoteDataSource {

    private val responses: ArrayDeque<Result<List<Photo>, DataError.Network>> =
        ArrayDeque<Result<List<Photo>, DataError.Network>>().apply { add(initialResponse) }

    private var nextGate: CompletableDeferred<Unit>? = null

    var fetchCount: Int = 0
        private set

    fun queueResponse(result: Result<List<Photo>, DataError.Network>) {
        responses.add(result)
    }

    fun pauseNextFetch() {
        nextGate = CompletableDeferred()
    }

    fun resumePausedFetch() {
        nextGate?.complete(Unit)
        nextGate = null
    }

    override suspend fun fetchPhotos(): Result<List<Photo>, DataError.Network> {
        fetchCount++
        nextGate?.await()
        return responses.removeFirstOrNull() ?: Result.Success(emptyList())
    }
}
