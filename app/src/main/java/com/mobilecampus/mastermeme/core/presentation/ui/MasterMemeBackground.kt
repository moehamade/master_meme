package com.mobilecampus.mastermeme.core.presentation.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobilecampus.mastermeme.ui.theme.MasterMemeTheme

@Composable
fun MasterMemeBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    MasterMemeTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = modifier.fillMaxSize(),
        ) {
            CompositionLocalProvider(LocalAbsoluteTonalElevation provides 0.dp) {
                content()
            }
        }
    }

}

@Preview
@Composable
fun MasterMemeBackgroundPreview() {
    MasterMemeBackground {
        Surface(color = MaterialTheme.colorScheme.background) {}
    }
}