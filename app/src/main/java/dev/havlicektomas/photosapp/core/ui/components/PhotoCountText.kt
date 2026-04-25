package dev.havlicektomas.photosapp.core.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.havlicektomas.photosapp.R
import dev.havlicektomas.photosapp.core.ui.theme.Fg4
import dev.havlicektomas.photosapp.core.ui.theme.PhotosAppTheme

@Composable
fun PhotoCountText(
    count: Int,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(R.string.home_photo_count, count),
        style = MaterialTheme.typography.labelSmall,
        color = Fg4,
        modifier = modifier,
    )
}

@Preview
@Composable
private fun PhotoCountTextPreview() {
    PhotosAppTheme { PhotoCountText(count = 1234) }
}
