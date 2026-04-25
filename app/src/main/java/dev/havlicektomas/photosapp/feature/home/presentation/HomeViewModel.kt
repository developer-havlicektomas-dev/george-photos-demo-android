package dev.havlicektomas.photosapp.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.havlicektomas.photosapp.core.domain.util.onFailure
import dev.havlicektomas.photosapp.core.domain.util.onSuccess
import dev.havlicektomas.photosapp.core.presentation.toUiText
import dev.havlicektomas.photosapp.feature.home.domain.Photo
import dev.havlicektomas.photosapp.feature.home.domain.PhotoRemoteDataSource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val photoRemoteDataSource: PhotoRemoteDataSource,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _events = Channel<HomeEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadPhotos(isRefresh = false)
    }

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.OnRefresh -> loadPhotos(isRefresh = true)

            HomeAction.OnFilterClick -> _state.update {
                it.copy(isFilterSheetOpen = true, draftTags = it.selectedTags)
            }

            HomeAction.OnSheetDismiss -> _state.update {
                it.copy(isFilterSheetOpen = false, draftTags = emptySet())
            }

            is HomeAction.OnDraftTagToggle -> _state.update {
                val draft = it.draftTags.toMutableSet().apply {
                    if (!add(action.tag)) remove(action.tag)
                }
                it.copy(draftTags = draft)
            }

            HomeAction.OnSheetClear -> _state.update { it.copy(draftTags = emptySet()) }

            HomeAction.OnSheetApply -> _state.update {
                val newSelected = it.draftTags
                it.copy(
                    selectedTags = newSelected,
                    filteredPhotos = applyFilter(it.photos, newSelected),
                    isFilterSheetOpen = false,
                    draftTags = emptySet(),
                )
            }

            is HomeAction.OnRemoveActiveFilter -> _state.update {
                val newSelected = it.selectedTags - action.tag
                it.copy(
                    selectedTags = newSelected,
                    filteredPhotos = applyFilter(it.photos, newSelected),
                )
            }

            HomeAction.OnClearAllActiveFilters -> _state.update {
                it.copy(selectedTags = emptySet(), filteredPhotos = it.photos)
            }

            is HomeAction.OnPhotoClick -> viewModelScope.launch {
                _events.send(HomeEvent.NavigateToDetail(action.photo))
            }
        }
    }

    private fun loadPhotos(isRefresh: Boolean) {
        viewModelScope.launch {
            _state.update {
                if (isRefresh) it.copy(isRefreshing = true, errorMessage = null)
                else it.copy(isLoading = true, errorMessage = null)
            }
            photoRemoteDataSource.fetchPhotos()
                .onSuccess { photos ->
                    _state.update {
                        it.copy(
                            photos = photos,
                            filteredPhotos = applyFilter(photos, it.selectedTags),
                            availableTags = deriveTags(photos),
                            isLoading = false,
                            isRefreshing = false,
                        )
                    }
                }
                .onFailure { error ->
                    val uiText = error.toUiText()
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = uiText,
                        )
                    }
                    _events.send(HomeEvent.ShowError(uiText))
                }
        }
    }

    private fun applyFilter(photos: List<Photo>, selected: Set<String>): List<Photo> =
        if (selected.isEmpty()) photos
        else photos.filter { photo -> photo.tags.any { it in selected } }

    private fun deriveTags(photos: List<Photo>): List<String> =
        photos.flatMap { it.tags }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
}
