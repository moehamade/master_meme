package com.mobilecampus.mastermeme.meme.presentation.screens.editor.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.mobilecampus.mastermeme.core.presentation.design_system.AppIcons
import com.mobilecampus.mastermeme.core.presentation.design_system.MasterMemeBackground
import com.mobilecampus.mastermeme.ui.theme.MasterMemeTheme


@Composable
fun MemeEditorBottomSheetContent(
    onSaveClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        ListItem(
            modifier = Modifier.clickable(onClick = onSaveClick),
            leadingContent = {
                Icon(
                    imageVector = AppIcons.save,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surfaceDim
                )
            }, headlineContent = {
                Text(
                    "Save to device", style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.surfaceDim
                    )
                )
            }, supportingContent = {
                Text(
                    text = "Save created meme in the Files of your device",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.outline
                    )

                )
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent
            )
        )
        ListItem(
            modifier = Modifier.clickable(onClick = onShareClick),
            leadingContent = {
                Icon(
                    imageVector = AppIcons.share,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surfaceDim

                )
            }, headlineContent = {
                Text(
                    "Share the meme", style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.surfaceDim
                    )
                )
            }, supportingContent = {
                Text(
                    text = "Share your meme or open it in the other App",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.outline
                    )
                )
            }, colors = ListItemDefaults.colors(
                containerColor = Color.Transparent
            )
        )
    }
}

@PreviewLightDark
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemeEditorBottomSheetPreview() {
    MasterMemeBackground {
        MasterMemeTheme {
            MemeEditorBottomSheetContent(
                onSaveClick = {},
                onShareClick = {}
            )
        }
    }

}