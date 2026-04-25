package dev.havlicektomas.photosapp.feature.detail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dev.havlicektomas.photosapp.core.navigation.DetailRoute
import dev.havlicektomas.photosapp.core.navigation.DetailRouteTypeMap
import dev.havlicektomas.photosapp.core.navigation.toPhoto
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val route: DetailRoute = savedStateHandle.toRoute<DetailRoute>(DetailRouteTypeMap)

    private val _state = MutableStateFlow(DetailState(photo = route.toPhoto()))
    val state = _state.asStateFlow()

    private val _events = Channel<DetailEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: DetailAction) {
        when (action) {
            DetailAction.OnBackClick -> viewModelScope.launch {
                _events.send(DetailEvent.NavigateBack)
            }
        }
    }
}
