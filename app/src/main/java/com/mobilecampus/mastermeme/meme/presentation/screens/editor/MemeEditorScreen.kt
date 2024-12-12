package com.mobilecampus.mastermeme.meme.presentation.screens.editor

import android.widget.Toast
import androidx.annotation.DrawableRes
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
fun MemeEditorScreenRoot(
    @DrawableRes backgroundImageResId: Int,
    viewModel: MemeEditorViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val state = viewModel.state

    // Observe saveCompleted events
    ObserveAsEvents(flow = viewModel.events) { event ->
        when (event) {
            is MemeEditorEvent.OnSaveResult -> {
                val success = event.success
                val path = event.filePath

                path?.let {
                    // if path is defined... vordead my friend this is the saved image
                    // do something here, or better do something back in the viewmodel
                    // so that you associate the local image path with room :)
                    // kaboom
                }

                val message = if (success) "Meme saved successfully!" else "Failed to save meme!"
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    MemeEditorScreen(
        resId = backgroundImageResId,
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun MemeEditorScreen(
    @DrawableRes resId: Int,
    state: MemeEditorState,
    onAction: (MemeEditorAction) -> Unit
) {

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = "Meme Background",
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .aspectRatio(1f, matchHeightConstraintsFirst = false)
                .onGloballyPositioned { coords ->
                    val position = coords.positionInRoot()
                    val size = coords.size
                    onAction(MemeEditorAction.UpdateImagePosition(position, size))
                },
            contentScale = ContentScale.Fit
        )

        state.textBoxes.forEach { textBox ->
            DraggableTextBox(
                textBox = textBox,
                imageOffset = state.imageOffset,
                imageSize = state.imageSize,
                onPositionChanged = { newPos ->
                    onAction(MemeEditorAction.UpdateTextBoxPosition(textBox.id, newPos))
                },
                onDelete = { onAction(MemeEditorAction.DeleteTextBox(textBox.id)) },
                onDoubleClick = { onAction(MemeEditorAction.StartEditingText(textBox)) },
                onSelect = { onAction(MemeEditorAction.SelectTextBox(textBox.id)) },
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
                    onClick = { onAction(MemeEditorAction.ToggleFont) },
                    enabled = state.currentEditingTextBox != null
                ) {
                    Text(
                        if (state.currentEditingTextBox?.style?.font == MemeFont.IMPACT) "Impact"
                        else "System"
                    )
                }

                Button(
                    onClick = { onAction(MemeEditorAction.SetFontSizeNormal) },
                    enabled = state.currentEditingTextBox != null
                ) {
                    Text("Normal")
                }

                Button(
                    onClick = { onAction(MemeEditorAction.SetFontSizeLarge) },
                    enabled = state.currentEditingTextBox != null
                ) {
                    Text("Large")
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onAction(MemeEditorAction.AddTextBox) }) {
                    Text("Add Text Box")
                }
                Button(onClick = { onAction(MemeEditorAction.SaveMeme(resId)) }) {
                    Text("Save Meme")
                }
            }
        }

        if (state.showEditDialog && state.currentEditingTextBox != null) {
            EditTextDialog(
                initialText = state.currentEditingTextBox.text,
                onDismiss = { onAction(MemeEditorAction.CancelEditing) },
                onConfirm = { newText -> onAction(MemeEditorAction.ConfirmTextChange(newText)) }
            )
        }
    }
}