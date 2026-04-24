package com.mdskun.dinnerdecider.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = AccentColor,
    secondary = BlueAccent,
    tertiary = PurpleAccent,
    background = WarmBackground,
    surface = White,
    onPrimary = White,
    onSecondary = White,
    onBackground = DarkText,
    onSurface = DarkText,
)

@Composable
fun DinnerDeciderTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}