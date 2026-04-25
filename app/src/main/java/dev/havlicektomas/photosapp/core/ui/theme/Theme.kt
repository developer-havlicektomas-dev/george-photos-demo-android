package dev.havlicektomas.photosapp.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    background = Bg,
    onBackground = Fg,
    surface = Surface,
    onSurface = Fg1,
    surfaceVariant = Surface2,
    onSurfaceVariant = Fg3,
    surfaceContainerLowest = Bg,
    surfaceContainerLow = BgAlt,
    surfaceContainer = Surface,
    surfaceContainerHigh = Surface2,
    surfaceContainerHighest = Surface2,
    outline = Border,
    outlineVariant = BorderSoft,
    primary = Accent,
    onPrimary = Bg,
    primaryContainer = AccentSoft2,
    onPrimaryContainer = AccentHover,
    secondary = AccentHover,
    onSecondary = Bg,
    error = ErrorRed,
    onError = Bg,
    scrim = Scrim,
)

@Composable
fun PhotosAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = PhotosTypography,
        shapes = PhotosShapes,
        content = content,
    )
}
