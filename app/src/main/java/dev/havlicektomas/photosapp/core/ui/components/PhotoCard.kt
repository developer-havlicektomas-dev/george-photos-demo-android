package dev.havlicektomas.photosapp.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import dev.havlicektomas.photosapp.core.ui.theme.Border
import dev.havlicektomas.photosapp.core.ui.theme.PhotosAppTheme
import dev.havlicektomas.photosapp.core.ui.theme.Surface

private const val MIN_PLACEHOLDER_RATIO = 0.7f
private const val MAX_PLACEHOLDER_RATIO = 1.5f

@Composable
fun PhotoCard(
    id: String,
    imageUrl: String,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val aspect = remember(id) { aspectFromId(id) }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Surface)
            .border(1.dp, Border, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(aspect),
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        0.4f to Color.Transparent,
                        0.78f to Color.Black.copy(alpha = 0.55f),
                        1f to Color.Black.copy(alpha = 0.85f),
                    ),
                ),
        )
        Text(
            text = title.ifBlank { "Untitled" },
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 10.dp, vertical = 8.dp),
        )
    }
}

private fun aspectFromId(id: String): Float {
    val hash = id.hashCode().toLong() and 0xFFFFFFFFL
    val span = MAX_PLACEHOLDER_RATIO - MIN_PLACEHOLDER_RATIO
    val fraction = (hash % 1000) / 1000f
    return MIN_PLACEHOLDER_RATIO + span * fraction
}

@Preview
@Composable
private fun PhotoCardPreview() {
    PhotosAppTheme {
        PhotoCard(
            id = "preview-1",
            imageUrl = "",
            title = "Sunset over the dunes — a long enough title to wrap",
            onClick = {},
            modifier = Modifier.padding(8.dp),
        )
    }
}
