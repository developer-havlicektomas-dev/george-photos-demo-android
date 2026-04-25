package dev.havlicektomas.photosapp.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.havlicektomas.photosapp.R
import dev.havlicektomas.photosapp.core.ui.theme.AccentHover
import dev.havlicektomas.photosapp.core.ui.theme.AccentRing
import dev.havlicektomas.photosapp.core.ui.theme.AccentSoft
import dev.havlicektomas.photosapp.core.ui.theme.AccentSoft2
import dev.havlicektomas.photosapp.core.ui.theme.PhotosAppTheme
import dev.havlicektomas.photosapp.core.ui.theme.PillShape

@Composable
fun RemovableTagChip(
    label: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
            .background(AccentSoft, PillShape)
            .border(1.dp, AccentRing, PillShape)
            .padding(start = 12.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = androidx.compose.ui.unit.TextUnit.Unspecified),
            color = AccentHover,
        )
        Box(
            modifier = Modifier
                .size(14.dp)
                .background(AccentSoft2, PillShape)
                .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.home_active_filter_remove, label),
                tint = AccentHover,
                modifier = Modifier.size(10.dp),
            )
        }
    }
}

@Preview
@Composable
private fun RemovableTagChipPreview() {
    PhotosAppTheme {
        RemovableTagChip(label = "nature", onRemove = {}, modifier = Modifier.padding(8.dp))
    }
}
