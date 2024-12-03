package com.mobilecampus.mastermeme.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush

@Immutable
data class ExtendedColorScheme(
    val buttonDefault: Brush,
    val buttonPressed: Brush
)

internal val LightExtendedColorScheme = ExtendedColorScheme(
    buttonDefault = ButtonDefaultGradient,
    buttonPressed = ButtonPressedGradient
)

internal val DarkExtendedColorScheme = ExtendedColorScheme(
    buttonDefault = ButtonDefaultGradient,
    buttonPressed = ButtonPressedGradient
)

internal val LocalExtendedColorScheme = staticCompositionLocalOf {
    LightExtendedColorScheme
}


object ExtendedTheme {
    val colorScheme: ExtendedColorScheme
        @ReadOnlyComposable
        @Composable
        get() = LocalExtendedColorScheme.current
}