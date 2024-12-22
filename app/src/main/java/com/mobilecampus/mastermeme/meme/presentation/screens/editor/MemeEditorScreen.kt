package com.mobilecampus.mastermeme.meme.presentation.screens.editor

import android.util.Log
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.mobilecampus.mastermeme.R
import com.mobilecampus.mastermeme.core.presentation.design_system.AppIcons
import com.mobilecampus.mastermeme.core.presentation.design_system.CenterAlignedAppTopAppBar
import com.mobilecampus.mastermeme.core.presentation.util.ObserveAsEvents
import com.mobilecampus.mastermeme.meme.domain.model.editor.MemeFont
import com.mobilecampus.mastermeme.meme.domain.model.editor.MemeTextColor
import com.mobilecampus.mastermeme.meme.domain.model.editor.MemeTextStyle
import com.mobilecampus.mastermeme.meme.domain.model.editor.TextBox
import com.mobilecampus.mastermeme.meme.presentation.screens.editor.components.AppSlider
import com.mobilecampus.mastermeme.meme.presentation.screens.editor.components.BottomBarLayout
import com.mobilecampus.mastermeme.meme.presentation.screens.editor.components.DraggableTextBox
import com.mobilecampus.mastermeme.meme.presentation.screens.editor.components.EditTextDialog
import com.mobilecampus.mastermeme.meme.presentation.screens.editor.components.MemeEditorBottomBar
import com.mobilecampus.mastermeme.meme.presentation.screens.editor.components.TextEditorOption.FontFamily
import com.mobilecampus.mastermeme.ui.theme.MasterMemeTheme
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

    ObserveAsEvents(flow = viewModel.events) { event ->
        when (event) {
            MemeEditorEvent.OnNavigateBack -> onNavigateBack()
            is MemeEditorEvent.OnSaveResult -> {
                val success = event.success
                val path = event.filePath
                path?.let {
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
        onAction = viewModel::onAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemeEditorScreen(
    @DrawableRes resId: Int,
    state: MemeEditorState,
    onAction: (MemeEditorAction) -> Unit,
) {
    val context = LocalContext.current
    val imageBitmap = ImageBitmap.imageResource(context.resources, resId)
    val imageAspectRatio = imageBitmap.width.toFloat() / imageBitmap.height.toFloat()

    Scaffold(
        topBar = {
            CenterAlignedAppTopAppBar(
                title = "Edit Meme",
                navigationIcon = {
                    IconButton(onClick = { onAction(MemeEditorAction.OnArrowBackClick) }) {
                        Icon(
                            imageVector = AppIcons.arrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            val currentLayout = if (state.currentEditingTextBox != null) {
                BottomBarLayout.TextEditor
            } else {
                BottomBarLayout.Default
            }

            val currentTextBox = state.currentEditingTextBox

            MemeEditorBottomBar(
                currentLayout = currentLayout,
                selectedColor = currentTextBox?.style?.color?.toFillColor() ?: Color.White,
                selectedFontFamily = androidx.compose.ui.text.font.FontFamily.Default, // This doesn't matter as we're just toggling
                selectedFontSize = currentTextBox?.style?.fontSize ?: 36f,
                canUndo = state.undoStack.isNotEmpty(),
                canRedo = state.redoStack.isNotEmpty(),
                onUndo = { onAction(MemeEditorAction.Undo) },
                onRedo = { onAction(MemeEditorAction.Redo) },
                onAddTextBox = { onAction(MemeEditorAction.AddTextBox) },
                onSaveMeme = { onAction(MemeEditorAction.SaveMeme(resId)) },
                onDismissTextEditor = { onAction(MemeEditorAction.CancelEditing) },
                onConfirmTextEdit = {
                    currentTextBox?.let { textBox ->
                        onAction(MemeEditorAction.ConfirmTextChange(textBox.text))
                    }
                },
                onFontFamilySelected = { onAction(MemeEditorAction.ToggleFont) },
                onFontSizeChanged = { onAction(MemeEditorAction.UpdateFontSize(it)) },
                onColorSelected = { color ->
                    val memeColor = when (color) {
                        Color.White -> MemeTextColor.WHITE
                        Color.Red -> MemeTextColor.RED
                        Color.Green -> MemeTextColor.GREEN
                        Color.Blue -> MemeTextColor.BLUE
                        else -> MemeTextColor.WHITE
                    }
                    onAction(MemeEditorAction.UpdateTextColor(memeColor))
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .consumeWindowInsets(paddingValues)
                .fillMaxSize()
        ) {
            // Background Image
            Image(
                painter = painterResource(id = resId),
                contentDescription = "Meme Background",
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .aspectRatio(imageAspectRatio)
                    .onGloballyPositioned { coords ->
                        val position = coords.positionInRoot()
                        val size = coords.size
                        val actualImageBounds = calculateActualImageBounds(size, imageAspectRatio)

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

            // Text Boxes
            state.textBoxes.forEach { textBox ->
                DraggableTextBox(
                    textBox = textBox,
                    imageOffset = state.imageOffset,
                    imageSize = state.imageSize,
                    onPositionChanged = { newPos ->
                        onAction(MemeEditorAction.UpdateTextBoxPosition(textBox.id, newPos))
                    },
                    onDelete = {
                        onAction(MemeEditorAction.DeleteTextBox(textBox.id))
                    },
                    onDoubleClick = {
                        onAction(MemeEditorAction.StartEditingText(textBox))
                    },
                    onSelect = {
                        onAction(MemeEditorAction.SelectTextBox(textBox.id))
                    },
                    isSelected = state.currentEditingTextBox?.id == textBox.id
                )
            }

            // Edit Dialog
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
}

/**
 * Calculates the visible area of the image within the provided container size,
 * ensuring no distortion (letterboxing).
 */
private fun calculateActualImageBounds(size: IntSize, imageAspectRatio: Float): IntRect {
    val containerAspectRatio = size.width.toFloat() / size.height.toFloat()
    return if (containerAspectRatio > imageAspectRatio) {
        // Image is height-constrained
        val actualWidth = size.height * imageAspectRatio
        val xOffset = ((size.width - actualWidth) / 2).roundToInt()
        IntRect(
            left = xOffset,
            top = 0,
            right = (xOffset + actualWidth).roundToInt(),
            bottom = size.height
        )
    } else {
        // Image is width-constrained
        val actualHeight = size.width / imageAspectRatio
        val yOffset = ((size.height - actualHeight) / 2).roundToInt()
        IntRect(
            left = 0,
            top = yOffset,
            right = size.width,
            bottom = (yOffset + actualHeight).roundToInt()
        )
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
                onAction = {},
            )
        }
    }
}
