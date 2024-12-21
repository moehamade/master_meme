package com.mobilecampus.mastermeme.meme.presentation.screens.editor.components

import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorIconButton(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) {
    CompositionLocalProvider(LocalRippleConfiguration provides MyRippleConfiguration) {

        IconButton(
            onClick = onClick,
            colors = IconButtonDefaults.iconButtonColors().copy(
                containerColor = backgroundColor,
                disabledContainerColor = backgroundColor,
                contentColor = MaterialTheme.colorScheme.surfaceDim,
                disabledContentColor = MaterialTheme.colorScheme.secondary.copy(alpha = .3f)
            ),
            enabled = enabled,
            modifier = modifier,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private val MyRippleConfiguration =
    RippleConfiguration(color = Color.White, rippleAlpha = RippleAlpha(.15f, .15f, .15f, .15f))

// https://developer.android.com/develop/ui/compose/touch-input/user-interactions/migrate-indication-ripple?hl=es-419#change-color-alpha-ripple
