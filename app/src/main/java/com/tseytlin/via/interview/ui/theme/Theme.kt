package com.tseytlin.via.interview.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ViaColorScheme = lightColorScheme(
    primary = ButtonBackground,
    onPrimary = Color.White,
    background = HomeBackground,
    onBackground = HomeTitleColor,
    surface = HomeBackground,
    onSurface = HomeTitleColor,
    surfaceVariant = DetailCardBackground,
    onSurfaceVariant = Color.White,
)

@Composable
fun ViaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ViaColorScheme,
        typography = ViaTypography,
        content = content,
    )
}
