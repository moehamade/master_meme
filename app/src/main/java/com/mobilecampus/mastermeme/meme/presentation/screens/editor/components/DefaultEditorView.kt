package com.mobilecampus.mastermeme.meme.presentation.screens.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobilecampus.mastermeme.core.presentation.design_system.AppIcons
import com.mobilecampus.mastermeme.ui.theme.MasterMemeTheme

@Composable
fun DefaultEditorView(
    modifier: Modifier = Modifier,
    undo: () -> Unit = {},
    redo: () -> Unit = {},
    addTextBox: () -> Unit = {},
    saveMeme: () -> Unit = {},
) {

    val backgroundColorModifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow)

    Row(
        modifier = modifier
            .height(75.dp)
            .then(backgroundColorModifier)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row {
            EditorIconButton(
                icon = AppIcons.undo,
                onClick = undo,
                enabled = false,
            )

            Spacer(modifier = Modifier.width(6.dp))

            EditorIconButton(
                icon = AppIcons.redo,
                onClick = redo,
                enabled = false,
            )
        }

        OutlinedButton(
            onClick = addTextBox,
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.outlinedButtonColors().copy(
                contentColor = MaterialTheme.colorScheme.onSurface,
            )
        ) {
            Text(
                text = "Add Text",
                style = MaterialTheme.typography.labelLarge
            )
        }

        Button(
            onClick = saveMeme,
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        ) {
            Text(
                text = "Save Meme",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Preview(showSystemUi = true, showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun DefaultEditorViewPreview() {
    MasterMemeTheme {
        DefaultEditorView(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight(align = Alignment.Bottom)
        )
    }
}
