package com.mobilecampus.mastermeme.meme.presentation.screens.editor

import android.graphics.BitmapFactory
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.mobilecampus.mastermeme.core.presentation.util.ObserveAsEvents
import com.mobilecampus.mastermeme.meme.domain.model.editor.MemeFont
import com.mobilecampus.mastermeme.meme.domain.model.editor.MemeTextColor
import com.mobilecampus.mastermeme.meme.presentation.screens.editor.components.AppSlider
import com.mobilecampus.mastermeme.meme.presentation.screens.editor.components.DraggableTextBox
import com.mobilecampus.mastermeme.meme.presentation.screens.editor.components.EditTextDialog
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@Composable
fun MemeEditorScreenRoot(
    @DrawableRes backgroundImageResId: Int,
    onNavigateBack: () -> Unit,
    viewModel: MemeEditorViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val state = viewModel.state

    // Observe events from the ViewModel and handle save results
    ObserveAsEvents(flow = viewModel.events) { event ->
        when (event) {
            is MemeEditorEvent.OnSaveResult -> {
                val success = event.success
                val path = event.filePath

                // If we have a valid file path, the save was successful
                path?.let {
                    onNavigateBack()
                }

                // Show feedback to the user about the save operation
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
    val context = LocalContext.current

    // Load the image and calculate its aspect ratio
    // This is crucial for proper sizing and text box positioning
    val imageBitmap = remember {
        BitmapFactory.decodeResource(context.resources, resId)
    }
    val imageAspectRatio = imageBitmap.width.toFloat() / imageBitmap.height.toFloat()

    Box(modifier = Modifier.fillMaxSize()) {
        // Track the actual bounds of the image within the layout
        var imageLayoutBounds by remember { mutableStateOf(IntRect.Zero) }

        // Background Image Component
        Image(
            painter = painterResource(id = resId),
            contentDescription = "Meme Background",
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                // Use the actual image aspect ratio to prevent distortion
                .aspectRatio(imageAspectRatio)
                // Monitor the image's position and size in the layout
                .onGloballyPositioned { coords ->
                    val position = coords.positionInRoot()
                    val size = coords.size

                    // Calculate the actual bounds of the image accounting for letterboxing
                    // This ensures text boxes stay within the visible image area
                    val containerAspectRatio = size.width.toFloat() / size.height.toFloat()
                    val actualImageBounds = if (containerAspectRatio > imageAspectRatio) {
                        // Image is height-constrained
                        val actualWidth = size.height * imageAspectRatio
                        val xOffset = (size.width - actualWidth) / 2
                        IntRect(
                            left = xOffset.roundToInt(),
                            top = 0,
                            right = (xOffset + actualWidth).roundToInt(),
                            bottom = size.height
                        )
                    } else {
                        // Image is width-constrained
                        val actualHeight = size.width / imageAspectRatio
                        val yOffset = (size.height - actualHeight) / 2
                        IntRect(
                            left = 0,
                            top = yOffset.roundToInt(),
                            right = size.width,
                            bottom = (yOffset + actualHeight).roundToInt()
                        )
                    }

                    // Update the layout bounds and notify the ViewModel
                    imageLayoutBounds = actualImageBounds
                    onAction(
                        MemeEditorAction.UpdateImagePosition(
                            offset = Offset(
                                x = position.x + actualImageBounds.left,
                                y = position.y + actualImageBounds.top
                            ),
                            size = IntSize(
                                width = actualImageBounds.width,
                                height = actualImageBounds.height
                            )
                        )
                    )
                },
            contentScale = ContentScale.Fit
        )

        // Render all text boxes
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

        // Bottom control panel for text editing and meme operations
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Track the currently selected text box to reset the font size slider
            var lastIdSelected by remember { mutableIntStateOf(state.currentEditingTextBox?.id ?: -1) }

            // Text editing controls - only shown when a text box is selected
            state.currentEditingTextBox?.let {
                var fontSize by remember { mutableFloatStateOf(it.style.fontSize) }

                // Reset font size when selecting a different text box
                if (lastIdSelected != it.id) {
                    lastIdSelected = it.id
                    fontSize = it.style.fontSize
                }

                // Font size slider
                AppSlider(
                    value = fontSize,
                    onValueChange = { newFontSize ->
                        fontSize = newFontSize
                        onAction(MemeEditorAction.UpdateFontSize(newFontSize))
                    },
                    valueRange = 24f..48f
                )

                // Text styling buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { onAction(MemeEditorAction.ToggleFont) }) {
                        Text(if (it.style.font == MemeFont.IMPACT) "Impact" else "System")
                    }
                    Button(onClick = {
                        onAction(MemeEditorAction.UpdateTextColor(MemeTextColor.WHITE))
                    }) {
                        Text("White")
                    }
                    Button(onClick = {
                        onAction(MemeEditorAction.UpdateTextColor(MemeTextColor.RED))
                    }) {
                        Text("Red")
                    }
                }
            }

            // Main action buttons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onAction(MemeEditorAction.AddTextBox) }) {
                    Text("Add Text Box")
                }
                Button(onClick = { onAction(MemeEditorAction.SaveMeme(resId)) }) {
                    Text("Save Meme")
                }
            }
        }

        // Text editing dialog
        if (state.showEditDialog && state.currentEditingTextBox != null) {
            EditTextDialog(
                initialText = state.currentEditingTextBox.text,
                onDismiss = { onAction(MemeEditorAction.CancelEditing) },
                onConfirm = { newText ->
                    onAction(MemeEditorAction.ConfirmTextChange(newText))
                }
            )
        }
    }
}