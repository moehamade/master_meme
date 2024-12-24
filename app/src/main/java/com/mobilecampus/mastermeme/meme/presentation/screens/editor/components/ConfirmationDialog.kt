package com.mobilecampus.mastermeme.meme.presentation.screens.editor.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mobilecampus.mastermeme.ui.theme.MasterMemeTheme
import com.mobilecampus.mastermeme.ui.theme.White

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    confirmTextButton: String,
    cancelTextButton: String,
    onDismiss: () -> Unit = { },
    onConfirm: () -> Unit = { },
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.onSurface)
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(color = White)
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.surfaceDim
                )
            ) {
                Text(
                    text = confirmTextButton,
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.surfaceDim)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.surfaceDim

                )
            ) {
                Text(
                    text = cancelTextButton,
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.surfaceDim)
                )
            }
        }
    )
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun ConfirmationDialogPreview() {
    MasterMemeTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            ConfirmationDialog(
                title = "Leave editor?",
                message = "You will lose your precious meme. If you're fine with that, press ‘Leave’.",
                confirmTextButton = "Leave",
                cancelTextButton = "Cancel",
            )
        }
    }
}
