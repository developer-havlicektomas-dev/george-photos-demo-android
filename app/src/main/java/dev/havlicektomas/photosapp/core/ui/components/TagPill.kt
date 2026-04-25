package dev.havlicektomas.photosapp.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.havlicektomas.photosapp.core.ui.theme.PhotosAppTheme
import dev.havlicektomas.photosapp.core.ui.theme.PillShape

@Composable
fun TagPill(
    label: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
        color = Color.White.copy(alpha = 0.85f),
        modifier = modifier
            .background(Color.White.copy(alpha = 0.08f), PillShape)
            .border(1.dp, Color.White.copy(alpha = 0.10f), PillShape)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}

@Preview
@Composable
private fun TagPillPreview() {
    PhotosAppTheme {
        TagPill(label = "sunset", modifier = Modifier.padding(8.dp))
    }
}
