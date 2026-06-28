package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BoldPrimary,
    secondary = BoldSecondary,
    tertiary = BoldCoral,
    background = Color(0xFF121417),
    surface = Color(0xFF1E2229),
    onPrimary = Color.White,
    onSecondary = BoldText,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2B313D),
    onSurfaceVariant = LightGray
)

private val LightColorScheme = lightColorScheme(
    primary = BoldPrimary,
    secondary = BoldSecondary,
    tertiary = BoldCoral,
    background = BoldBackground,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = BoldText,
    onTertiary = Color.White,
    onBackground = BoldText,
    onSurface = BoldText,
    surfaceVariant = BoldSurface,
    onSurfaceVariant = BoldSubdued
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Default to false for the premium, high-contrast light "Bold Typography" style!
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
