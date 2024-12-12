package com.mobilecampus.mastermeme.meme.presentation.screens.editor

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mobilecampus.mastermeme.core.presentation.design_system.ObserveAsEvents
import com.mobilecampus.mastermeme.meme.domain.model.editor.MemeFont
import com.mobilecampus.mastermeme.meme.presentation.screens.editor.components.DraggableTextBox
import com.mobilecampus.mastermeme.meme.presentation.screens.editor.components.EditTextDialog
import org.koin.androidx.compose.koinViewModel

@Composable
fun MemeEditorScreen(
    backgroundImageResId: Int,
    viewModel: MemeEditorViewModel = koinViewModel()
) {
    val context = LocalContext.current

    val state = viewModel.state

    // Observe saveCompleted events
    ObserveAsEvents(flow = viewModel.saveCompleted) { saveSuccess ->
        saveSuccess?.let {
            val message = if (it) "Meme saved successfully!" else "Failed to save meme!"
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = backgroundImageResId),
            contentDescription = "Meme Background",
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .aspectRatio(1f, matchHeightConstraintsFirst = false)
                .onGloballyPositioned { coords ->
                    val position = coords.positionInRoot()
                    val size = coords.size
                    viewModel.onEvent(MemeEditorEvent.UpdateImagePosition(position, size))
                },
            contentScale = ContentScale.Fit
        )

        state.textBoxes.forEach { textBox ->
            DraggableTextBox(
                textBox = textBox,
                imageOffset = state.imageOffset,
                imageSize = state.imageSize,
                onPositionChanged = { newPos ->
                    viewModel.onEvent(MemeEditorEvent.UpdateTextBoxPosition(textBox.id, newPos))
                },
                onDelete = { viewModel.onEvent(MemeEditorEvent.DeleteTextBox(textBox.id)) },
                onDoubleClick = { viewModel.onEvent(MemeEditorEvent.StartEditingText(textBox)) },
                onSelect = { viewModel.onEvent(MemeEditorEvent.SelectTextBox(textBox.id)) },
                isSelected = state.currentEditingTextBox?.id == textBox.id
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { viewModel.onEvent(MemeEditorEvent.ToggleFont) },
                    enabled = state.currentEditingTextBox != null
                ) {
                    Text(
                        if (state.currentEditingTextBox?.style?.font == MemeFont.IMPACT) "Impact"
                        else "System"
                    )
                }

                Button(
                    onClick = { viewModel.onEvent(MemeEditorEvent.SetFontSizeNormal) },
                    enabled = state.currentEditingTextBox != null
                ) {
                    Text("Normal")
                }

                Button(
                    onClick = { viewModel.onEvent(MemeEditorEvent.SetFontSizeLarge) },
                    enabled = state.currentEditingTextBox != null
                ) {
                    Text("Large")
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { viewModel.onEvent(MemeEditorEvent.AddTextBox) }) {
                    Text("Add Text Box")
                }
                Button(onClick = { viewModel.onEvent(MemeEditorEvent.SaveMeme) }) {
                    Text("Save Meme")
                }
            }
        }

        if (state.showEditDialog && state.currentEditingTextBox != null) {
            EditTextDialog(
                initialText = state.currentEditingTextBox.text,
                onDismiss = { viewModel.onEvent(MemeEditorEvent.CancelEditing) },
                onConfirm = { newText -> viewModel.onEvent(MemeEditorEvent.ConfirmTextChange(newText)) }
            )
        }
    }
}