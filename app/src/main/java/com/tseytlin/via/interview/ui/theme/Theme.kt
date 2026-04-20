package com.tseytlin.via.interview.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ViaColorScheme = lightColorScheme(
    primary = ButtonBackground,
    background = HomeBackground,
    surface = HomeBackground,
)

@Composable
fun ViaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ViaColorScheme,
        content = content,
    )
}
