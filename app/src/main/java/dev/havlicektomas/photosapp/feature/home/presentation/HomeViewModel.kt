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
            HomeAction.OnRefresh -> loadPhotos(
                isRefresh = true,
                selectedTags = _state.value.selectedTags,
            )

            HomeAction.OnFilterClick -> _state.update {
                it.copy(isFilterSheetOpen = true, draftTags = it.selectedTags)
            }

            HomeAction.OnSheetDismiss -> _state.update {
                it.copy(isFilterSheetOpen = false, draftTags = emptySet(), tagInput = "")
            }

            is HomeAction.OnDraftTagToggle -> _state.update {
                val draft = it.draftTags.toMutableSet().apply {
                    if (!add(action.tag)) remove(action.tag)
                }
                it.copy(draftTags = draft)
            }

            HomeAction.OnSheetClear -> _state.update {
                it.copy(draftTags = emptySet(), tagInput = "")
            }

            is HomeAction.OnTagInputChange -> _state.update {
                it.copy(tagInput = action.value)
            }

            HomeAction.OnAddTypedTag -> _state.update {
                val trimmed = it.tagInput.trim()
                if (trimmed.isEmpty()) it
                else it.copy(draftTags = it.draftTags + trimmed, tagInput = "")
            }

            HomeAction.OnSheetApply -> {
                val newSelected = _state.value.draftTags
                _state.update {
                    it.copy(
                        selectedTags = newSelected,
                        isFilterSheetOpen = false,
                        draftTags = emptySet(),
                        tagInput = "",
                    )
                }
                loadPhotos(isRefresh = false, selectedTags = newSelected)
            }

            is HomeAction.OnRemoveActiveFilter -> {
                val newSelected = _state.value.selectedTags - action.tag
                _state.update { it.copy(selectedTags = newSelected) }
                loadPhotos(isRefresh = false, selectedTags = newSelected)
            }

            HomeAction.OnClearAllActiveFilters -> {
                _state.update { it.copy(selectedTags = emptySet()) }
                loadPhotos(isRefresh = false, selectedTags = emptySet())
            }

            is HomeAction.OnPhotoClick -> viewModelScope.launch {
                _events.send(HomeEvent.NavigateToDetail(action.photo))
            }
        }
    }

    private fun loadPhotos(
        isRefresh: Boolean,
        selectedTags: Set<String> = emptySet()
    ) {
        viewModelScope.launch {
            _state.update {
                if (isRefresh) it.copy(isRefreshing = true, errorMessage = null)
                else it.copy(isLoading = true, errorMessage = null)
            }
            photoRemoteDataSource.fetchPhotos(selectedTags.toList())
                .onSuccess { photos ->
                    _state.update {
                        it.copy(
                            photos = photos,
                            filteredPhotos = photos,
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

    private fun deriveTags(photos: List<Photo>): List<String> =
        photos.flatMap { it.tags }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
}
