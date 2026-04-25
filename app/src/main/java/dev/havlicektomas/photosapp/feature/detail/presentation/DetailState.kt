package dev.havlicektomas.photosapp.feature.detail.presentation

import androidx.compose.runtime.Stable
import dev.havlicektomas.photosapp.feature.home.domain.Photo

@Stable
data class DetailState(
    val photo: Photo,
)

sealed interface DetailAction {
    data object OnBackClick : DetailAction
}

sealed interface DetailEvent {
    data object NavigateBack : DetailEvent
}
