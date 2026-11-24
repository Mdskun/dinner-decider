package com.mdskun.dinnerdecider.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = AccentColor,
    secondary = BlueAccent,
    tertiary = PurpleAccent,
    background = WarmBackground,
    surface = White,
    surfaceVariant = CardBackground,
    onPrimary = White,
    onSecondary = White,
    onBackground = DarkText,
    onSurface = DarkText,
    onSurfaceVariant = SubtitleText,
    outlineVariant = CardBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = AccentColor,
    secondary = BlueAccent,
    tertiary = PurpleAccent,
    background = DarkWarmBackground,
    surface = DarkSurface,
    surfaceVariant = DarkCardBackground,
    onPrimary = White,
    onSecondary = White,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    onSurfaceVariant = DarkSubtitleText,
    outlineVariant = DarkCardBorder
)

@Composable
fun DinnerDeciderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}