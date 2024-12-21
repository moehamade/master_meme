package com.mobilecampus.mastermeme.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    surfaceContainerLowest = Onyx,
    surfaceContainerLow = Mirage,
    surfaceContainer = Mirage,
    surfaceContainerHigh = BalticSea,
    outline = Boulder,
    primary = ButterflyBush,
    surfaceDim = Lavender,
    onSurface = LavenderPinocchio,
    primaryContainer = LavenderPink,
    surfaceContainerHighest = LavenderMist,
    error = CornellRed,
    onPrimary = CherryPie,
    background = Onyx,
    onPrimaryContainer = Black
)

private val LightColorScheme = lightColorScheme(
    surfaceContainerLowest = Onyx,
    surfaceContainerLow = Mirage,
    surfaceContainer = Mirage,
    surfaceContainerHigh = BalticSea,
    outline = Boulder,
    primary = ButterflyBush,
    surfaceDim = Lavender,
    onSurface = LavenderPinocchio,
    primaryContainer = LavenderPink,
    surfaceContainerHighest = LavenderMist,
    error = CornellRed,
    onPrimary = CherryPie,
    background = Onyx,
    onPrimaryContainer = Black
)

@Composable
fun MasterMemeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val extendedColorScheme = if (darkTheme) DarkExtendedColorScheme else LightExtendedColorScheme

    CompositionLocalProvider(LocalExtendedColorScheme provides extendedColorScheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }

}