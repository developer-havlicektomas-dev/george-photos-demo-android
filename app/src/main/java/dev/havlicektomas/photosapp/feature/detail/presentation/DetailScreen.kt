package dev.havlicektomas.photosapp.feature.detail.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import dev.havlicektomas.photosapp.R
import dev.havlicektomas.photosapp.core.presentation.ObserveAsEvents
import dev.havlicektomas.photosapp.core.ui.components.GhostIconButton
import dev.havlicektomas.photosapp.core.ui.components.TagPill
import dev.havlicektomas.photosapp.core.ui.theme.Bg
import dev.havlicektomas.photosapp.core.ui.theme.PhotosAppTheme
import dev.havlicektomas.photosapp.feature.home.domain.Photo
import org.koin.androidx.compose.koinViewModel

@Composable
fun DetailRoot(
    onNavigateBack: () -> Unit,
    viewModel: DetailViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            DetailEvent.NavigateBack -> onNavigateBack()
        }
    }

    BackHandler { viewModel.onAction(DetailAction.OnBackClick) }

    DetailScreen(state = state, onAction = viewModel::onAction)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailScreen(
    state: DetailState,
    onAction: (DetailAction) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg),
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(state.photo.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = state.photo.title,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize(),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .background(
                    Brush.verticalGradient(
                        0f to Color.Black.copy(alpha = 0.65f),
                        1f to Color.Transparent,
                    ),
                )
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 28.dp),
        ) {
            GhostIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.detail_back),
                onClick = { onAction(DetailAction.OnBackClick) },
                iconTint = Color.White,
                backgroundColor = Color.Black.copy(alpha = 0.35f),
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.5f to Color.Black.copy(alpha = 0.55f),
                        1f to Color.Black.copy(alpha = 0.85f),
                    ),
                )
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(start = 20.dp, end = 20.dp, top = 60.dp, bottom = 20.dp),
        ) {
            Text(
                text = state.photo.title.ifBlank { "Untitled" },
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
            )
            if (state.photo.tags.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    state.photo.tags.forEach { tag ->
                        TagPill(label = tag)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun DetailScreenPreview() {
    PhotosAppTheme {
        DetailScreen(
            state = DetailState(
                photo = Photo(
                    id = "1",
                    title = "Sunset over the ocean — long captioned title",
                    imageUrl = "",
                    tags = listOf("nature", "sunset", "ocean", "landscape"),
                ),
            ),
            onAction = {},
        )
    }
}
