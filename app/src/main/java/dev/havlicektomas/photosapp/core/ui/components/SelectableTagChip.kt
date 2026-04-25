package dev.havlicektomas.photosapp.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.havlicektomas.photosapp.core.ui.theme.AccentHover
import dev.havlicektomas.photosapp.core.ui.theme.AccentSoft
import dev.havlicektomas.photosapp.core.ui.theme.Border
import dev.havlicektomas.photosapp.core.ui.theme.Fg2
import dev.havlicektomas.photosapp.core.ui.theme.PhotosAppTheme
import dev.havlicektomas.photosapp.core.ui.theme.PillShape
import dev.havlicektomas.photosapp.core.ui.theme.Surface

@Composable
fun SelectableTagChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val background = if (selected) AccentSoft else Surface
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else Border
    val contentColor = if (selected) AccentHover else Fg2

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
            .background(background, PillShape)
            .border(1.dp, borderColor, PillShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        AnimatedVisibility(visible = selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(14.dp),
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
        )
    }
}

@Preview
@Composable
private fun SelectableTagChipPreview() {
    PhotosAppTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(8.dp)) {
            SelectableTagChip(label = "nature", selected = true, onClick = {})
            SelectableTagChip(label = "forest", selected = false, onClick = {})
        }
    }
}
