package com.meva.notes

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object MevaPalette {
    val primary = Color(0xFF325346)
    val primaryContainer = Color(0xFF4A6B5D)
    val primaryFixed = Color(0xFFC6EBD9)
    val primaryFixedDim = Color(0xFFABCEBE)
    val secondary = Color(0xFF944931)
    val secondaryContainer = Color(0xFFFD9D80)
    val secondaryFixed = Color(0xFFFFDBD0)
    val tertiary = Color(0xFF474E4B)
    val tertiaryContainer = Color(0xFF5F6662)
    val tertiaryFixed = Color(0xFFDDE4DF)
    val background = Color(0xFFF9F9F7)
    val surfaceLow = Color(0xFFF4F4F2)
    val surface = Color(0xFFEEEEEC)
    val surfaceHigh = Color(0xFFE8E8E6)
    val surfaceHighest = Color(0xFFE2E3E1)
    val outline = Color(0xFF727974)
    val outlineVariant = Color(0xFFC1C8C3)
    val onSurface = Color(0xFF1A1C1B)
    val onSurfaceVariant = Color(0xFF414844)
    val canvas = Color(0xFFEAE8E3)
    val error = Color(0xFFBA1A1A)
    val errorContainer = Color(0xFFFFDAD6)
    val inverseSurface = Color(0xFF2F3130)
    val inverseOnSurface = Color(0xFFF1F1EF)
}

private val MevaColorScheme = lightColorScheme(
    primary = MevaPalette.primary,
    onPrimary = Color.White,
    primaryContainer = MevaPalette.primaryContainer,
    onPrimaryContainer = Color(0xFFC6EAD8),
    secondary = MevaPalette.secondary,
    onSecondary = Color.White,
    secondaryContainer = MevaPalette.secondaryContainer,
    onSecondaryContainer = Color(0xFF77331D),
    tertiary = MevaPalette.tertiary,
    onTertiary = Color.White,
    tertiaryContainer = MevaPalette.tertiaryContainer,
    onTertiaryContainer = MevaPalette.tertiaryFixed,
    error = MevaPalette.error,
    errorContainer = MevaPalette.errorContainer,
    onError = Color.White,
    background = MevaPalette.background,
    onBackground = MevaPalette.onSurface,
    surface = MevaPalette.background,
    onSurface = MevaPalette.onSurface,
    surfaceVariant = MevaPalette.surfaceHighest,
    onSurfaceVariant = MevaPalette.onSurfaceVariant,
    outline = MevaPalette.outline,
    outlineVariant = MevaPalette.outlineVariant,
    inverseSurface = MevaPalette.inverseSurface,
    inverseOnSurface = MevaPalette.inverseOnSurface,
    inversePrimary = MevaPalette.primaryFixedDim
)

private val MevaTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 39.sp,
        letterSpacing = (-0.6).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 29.sp,
        letterSpacing = (-0.25).sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 25.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 14.sp,
        lineHeight = 21.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 12.sp,
        lineHeight = 18.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.15.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.3.sp
    )
)

@Composable
fun MevaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MevaColorScheme,
        typography = MevaTypography,
        content = content
    )
}
