package dev.havlicektomas.photosapp.feature.home.presentation

import androidx.compose.runtime.Stable
import dev.havlicektomas.photosapp.core.presentation.UiText
import dev.havlicektomas.photosapp.feature.home.domain.Photo

@Stable
data class HomeState(
    val photos: List<Photo> = emptyList(),
    val filteredPhotos: List<Photo> = emptyList(),
    val availableTags: List<String> = emptyList(),
    val selectedTags: Set<String> = emptySet(),
    val draftTags: Set<String> = emptySet(),
    val tagInput: String = "",
    val isFilterSheetOpen: Boolean = false,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: UiText? = null,
)

sealed interface HomeAction {
    data object OnRefresh : HomeAction
    data object OnFilterClick : HomeAction
    data object OnSheetDismiss : HomeAction
    data class OnDraftTagToggle(val tag: String) : HomeAction
    data object OnSheetClear : HomeAction
    data object OnSheetApply : HomeAction
    data class OnTagInputChange(val value: String) : HomeAction
    data object OnAddTypedTag : HomeAction
    data class OnRemoveActiveFilter(val tag: String) : HomeAction
    data object OnClearAllActiveFilters : HomeAction
    data class OnPhotoClick(val photo: Photo) : HomeAction
}

sealed interface HomeEvent {
    data class NavigateToDetail(val photo: Photo) : HomeEvent
    data class ShowError(val message: UiText) : HomeEvent
}
