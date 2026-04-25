package dev.havlicektomas.photosapp.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import dev.havlicektomas.photosapp.R

@OptIn(ExperimentalTextApi::class)
private fun interFont(weight: Int, fontWeight: FontWeight) = Font(
    resId = R.font.inter_variable,
    weight = fontWeight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

internal val Inter = FontFamily(
    interFont(300, FontWeight.Light),
    interFont(400, FontWeight.Normal),
    interFont(500, FontWeight.Medium),
    interFont(600, FontWeight.SemiBold),
    interFont(700, FontWeight.Bold),
    interFont(800, FontWeight.ExtraBold),
)

private val baseStyle = TextStyle(fontFamily = Inter)

internal val PhotosTypography = Typography(
    headlineSmall = baseStyle.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.015).em,
    ),
    titleLarge = baseStyle.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        lineHeight = 20.sp,
        letterSpacing = (-0.01).em,
    ),
    titleMedium = baseStyle.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 18.sp,
        letterSpacing = (-0.01).em,
    ),
    bodyLarge = baseStyle.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = baseStyle.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelLarge = baseStyle.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 14.sp,
    ),
    labelMedium = baseStyle.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 17.sp,
        letterSpacing = (-0.005).em,
    ),
    labelSmall = baseStyle.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 12.sp,
        letterSpacing = 0.04.em,
    ),
)

internal val HandleLabel: TextStyle = baseStyle.copy(
    fontWeight = FontWeight.Normal,
    fontSize = 10.sp,
    lineHeight = 12.sp,
    letterSpacing = 0.04.em,
)
