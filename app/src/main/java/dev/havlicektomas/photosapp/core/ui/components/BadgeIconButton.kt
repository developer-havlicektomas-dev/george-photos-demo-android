package dev.havlicektomas.photosapp.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.havlicektomas.photosapp.core.ui.theme.AccentRing
import dev.havlicektomas.photosapp.core.ui.theme.AccentSoft
import dev.havlicektomas.photosapp.core.ui.theme.Bg
import dev.havlicektomas.photosapp.core.ui.theme.Border
import dev.havlicektomas.photosapp.core.ui.theme.Fg3
import dev.havlicektomas.photosapp.core.ui.theme.PhotosAppTheme

@Composable
fun BadgeIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeCount: Int = 0,
    active: Boolean = badgeCount > 0,
    iconTint: Color = if (active) MaterialTheme.colorScheme.primary else Fg3,
    backgroundColor: Color = if (active) AccentSoft else Color.Transparent,
    borderColor: Color = if (active) AccentRing else Color.Transparent,
) {
    Box(modifier = modifier.size(38.dp), contentAlignment = Alignment.Center) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(38.dp)
                .background(backgroundColor, RoundedCornerShape(10.dp))
                .border(1.dp, borderColor, RoundedCornerShape(10.dp)),
            colors = IconButtonDefaults.iconButtonColors(contentColor = iconTint),
        ) {
            Icon(imageVector = icon, contentDescription = contentDescription)
        }
        if (badgeCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .defaultMinSize(minWidth = 16.dp, minHeight = 16.dp)
                    .border(2.dp, Bg, CircleShape)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = badgeCount.toString(),
                    color = Bg,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    lineHeight = 10.sp,
                )
            }
        }
    }
}

@Composable
fun GhostIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    backgroundColor: Color = Color.Transparent,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(38.dp)
            .background(backgroundColor, RoundedCornerShape(10.dp))
            .border(1.dp, Border, RoundedCornerShape(10.dp)),
        colors = IconButtonDefaults.iconButtonColors(contentColor = iconTint),
    ) {
        Icon(imageVector = icon, contentDescription = contentDescription)
    }
}

@Preview
@Composable
private fun BadgeIconButtonPreview() {
    PhotosAppTheme {
        BadgeIconButton(
            icon = Icons.Default.FilterList,
            contentDescription = null,
            onClick = {},
            badgeCount = 3,
        )
    }
}
