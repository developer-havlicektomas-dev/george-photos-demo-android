package dev.havlicektomas.photosapp.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.havlicektomas.photosapp.R
import dev.havlicektomas.photosapp.core.presentation.ObserveAsEvents
import dev.havlicektomas.photosapp.core.ui.components.BadgeIconButton
import dev.havlicektomas.photosapp.core.ui.components.BrandTitle
import dev.havlicektomas.photosapp.core.ui.components.PhotoCard
import dev.havlicektomas.photosapp.core.ui.components.PhotoCountText
import dev.havlicektomas.photosapp.core.ui.components.RemovableTagChip
import dev.havlicektomas.photosapp.core.ui.theme.Bg
import dev.havlicektomas.photosapp.core.ui.theme.Fg4
import dev.havlicektomas.photosapp.core.ui.theme.PhotosAppTheme
import dev.havlicektomas.photosapp.core.ui.theme.Surface
import dev.havlicektomas.photosapp.feature.home.domain.Photo
import dev.havlicektomas.photosapp.feature.home.presentation.components.FilterBottomSheet
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeRoot(
    onNavigateToDetail: (Photo) -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is HomeEvent.NavigateToDetail -> onNavigateToDetail(event.photo)
            is HomeEvent.ShowError -> scope.launch {
                snackbarHostState.showSnackbar(event.message.asString(context))
            }
        }
    }

    HomeScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onAction = viewModel::onAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeState,
    snackbarHostState: SnackbarHostState,
    onAction: (HomeAction) -> Unit,
) {
    val pullState = rememberPullToRefreshState()
    val columns = rememberColumnCount()

    Scaffold(
        containerColor = Bg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            HomeTopBar(
                photoCount = state.filteredPhotos.size,
                activeFilterCount = state.selectedTags.size,
                onFilterClick = { onAction(HomeAction.OnFilterClick) },
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(Bg)) {

            if (state.selectedTags.isNotEmpty()) {
                ActiveFilterStrip(
                    activeTags = state.selectedTags.toList().sorted(),
                    onRemove = { onAction(HomeAction.OnRemoveActiveFilter(it)) },
                    onClearAll = { onAction(HomeAction.OnClearAllActiveFilters) },
                )
            }

            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { onAction(HomeAction.OnRefresh) },
                state = pullState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                indicator = {
                    PullToRefreshDefaults.Indicator(
                        state = pullState,
                        isRefreshing = state.isRefreshing,
                        color = MaterialTheme.colorScheme.primary,
                        containerColor = Surface,
                        modifier = Modifier.align(Alignment.TopCenter),
                    )
                },
            ) {
                if (state.filteredPhotos.isEmpty() && !state.isLoading) {
                    HomeEmptyState()
                } else {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(columns),
                        contentPadding = PaddingValues(8.dp),
                        verticalItemSpacing = 8.dp,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(items = state.filteredPhotos, key = { it.id }) { photo ->
                            PhotoCard(
                                id = photo.id,
                                imageUrl = photo.imageUrl,
                                title = photo.title,
                                onClick = { onAction(HomeAction.OnPhotoClick(photo)) },
                            )
                        }
                    }
                }
            }
        }

        if (state.isFilterSheetOpen) {
            FilterBottomSheet(
                availableTags = state.availableTags,
                draftTags = state.draftTags,
                onToggleTag = { onAction(HomeAction.OnDraftTagToggle(it)) },
                onClear = { onAction(HomeAction.OnSheetClear) },
                onCancel = { onAction(HomeAction.OnSheetDismiss) },
                onApply = { onAction(HomeAction.OnSheetApply) },
            )
        }
    }
}

@Composable
private fun HomeTopBar(
    photoCount: Int,
    activeFilterCount: Int,
    onFilterClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(Bg)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp)
            .padding(top = 14.dp, bottom = 12.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f),
        ) {
            BrandTitle()
            PhotoCountText(count = photoCount)
        }
        BadgeIconButton(
            icon = Icons.Default.FilterList,
            contentDescription = stringResource(R.string.home_filter_action),
            onClick = onFilterClick,
            badgeCount = activeFilterCount,
        )
    }
}

@Composable
private fun ActiveFilterStrip(
    activeTags: List<String>,
    onRemove: (String) -> Unit,
    onClearAll: () -> Unit,
) {
    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(Bg),
    ) {
        items(items = activeTags, key = { it }) { tag ->
            RemovableTagChip(label = tag, onRemove = { onRemove(tag) })
        }
        item {
            TextButton(onClick = onClearAll) {
                Text(
                    text = stringResource(R.string.home_clear_active_filters),
                    style = MaterialTheme.typography.labelSmall,
                    color = Fg4,
                )
            }
        }
    }
}

@Composable
private fun HomeEmptyState() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
    ) {
        Text(
            text = stringResource(R.string.home_empty_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = stringResource(R.string.home_empty_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = Fg4,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun rememberColumnCount(): Int {
    val widthDp = LocalConfiguration.current.screenWidthDp
    return when {
        widthDp < 600 -> 2
        widthDp < 840 -> 3
        else -> 4
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    PhotosAppTheme {
        HomeScreen(
            state = HomeState(
                photos = previewPhotos(),
                filteredPhotos = previewPhotos(),
                availableTags = listOf("nature", "forest", "sunset", "ocean"),
                selectedTags = setOf("nature"),
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onAction = {},
        )
    }
}

private fun previewPhotos(): List<Photo> = List(8) { i ->
    Photo(
        id = "p$i",
        title = "Photo $i — a sample title",
        imageUrl = "",
        tags = listOf("nature", "forest").take(1 + i % 2),
    )
}
