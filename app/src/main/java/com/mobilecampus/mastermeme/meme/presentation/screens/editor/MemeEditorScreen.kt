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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobilecampus.mastermeme.R
import com.mobilecampus.mastermeme.core.presentation.util.ObserveAsEvents
import com.mobilecampus.mastermeme.meme.domain.model.editor.MemeFont
import com.mobilecampus.mastermeme.meme.domain.model.editor.MemeTextColor
import com.mobilecampus.mastermeme.meme.domain.model.editor.MemeTextStyle
import com.mobilecampus.mastermeme.meme.domain.model.editor.TextBox
import com.mobilecampus.mastermeme.meme.presentation.screens.editor.components.AppSlider
import com.mobilecampus.mastermeme.meme.presentation.screens.editor.components.DraggableTextBox
import com.mobilecampus.mastermeme.meme.presentation.screens.editor.components.EditTextDialog
import com.mobilecampus.mastermeme.ui.theme.MasterMemeTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun MemeEditorScreenRoot(
    @DrawableRes backgroundImageResId: Int,
    onNavigateBack: () -> Unit,
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
                    onNavigateBack()
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
    onAction: (MemeEditorAction) -> Unit = {},
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
            // keep track of previously selected textbox, so we reset AppSlider positions
            var lastIdSelected by remember {
                mutableIntStateOf(
                    state.currentEditingTextBox?.id ?: -1
                )
            }
            state.currentEditingTextBox?.let {
                var fontSize by remember { mutableFloatStateOf(it.style.fontSize) }

                if (lastIdSelected != it.id) {
                    lastIdSelected = it.id
                    fontSize = it.style.fontSize
                }

                AppSlider(
                    value = fontSize,
                    onValueChange = { newFontSize ->
                        fontSize = newFontSize
                        onAction(MemeEditorAction.UpdateFontSize(newFontSize))
                    },
                    valueRange = 24f..48f
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { onAction(MemeEditorAction.ToggleFont) }
                    ) {
                        Text(
                            if (state.currentEditingTextBox.style.font == MemeFont.IMPACT) "Impact"
                            else "System"
                        )
                    }

                    Button(
                        onClick = { onAction(MemeEditorAction.UpdateTextColor(MemeTextColor.WHITE)) }
                    ) {
                        Text("White")
                    }

                    Button(
                        onClick = { onAction(MemeEditorAction.UpdateTextColor(MemeTextColor.RED)) }
                    ) {
                        Text("Red")
                    }
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

@Preview(showBackground = true)
@Composable
private fun MemeEditorScreenPreview() {
    MasterMemeTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            MemeEditorScreen(
                resId = R.drawable.meme_template_01,
                state = MemeEditorState().copy(
                    showEditDialog = true,
                    textBoxes = listOf(
                        TextBox(
                            id = 0,
                            text = "Hello, World!",
                            position = Offset(150f, 850f),
                            style = MemeTextStyle(
                                font = MemeFont.SYSTEM,
                                fontSize = 34f,
                                color = MemeTextColor.WHITE
                            )
                        )
                    )
                ),
            )
        }
    }
}
