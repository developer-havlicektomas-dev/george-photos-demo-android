package dev.havlicektomas.photosapp.core.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.havlicektomas.photosapp.R
import dev.havlicektomas.photosapp.core.ui.theme.Fg
import dev.havlicektomas.photosapp.core.ui.theme.PhotosAppTheme

@Composable
fun BrandTitle(
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        Text(
            text = stringResource(R.string.brand_name),
            style = MaterialTheme.typography.titleLarge,
            color = Fg,
        )
        Text(
            text = stringResource(R.string.brand_dot),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Preview
@Composable
private fun BrandTitlePreview() {
    PhotosAppTheme { BrandTitle() }
}
