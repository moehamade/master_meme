package com.mobilecampus.mastermeme.meme.presentation.screens.editor

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobilecampus.mastermeme.meme.domain.model.editor.MemeFont
import com.mobilecampus.mastermeme.meme.domain.model.editor.MemeTextColor
import com.mobilecampus.mastermeme.meme.domain.model.editor.MemeTextStyle
import com.mobilecampus.mastermeme.meme.domain.model.editor.TextBox
import com.mobilecampus.mastermeme.meme.domain.use_case.SaveMemeUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class MemeEditorState(
    val textBoxes: List<TextBox> = emptyList(),
    val currentEditingTextBox: TextBox? = null,
    val showEditDialog: Boolean = false,
    val imageOffset: Offset = Offset.Zero,
    val imageSize: IntSize = IntSize.Zero,
    val undoStack: List<EditorAction> = emptyList(),
    val redoStack: List<EditorAction> = emptyList()
)

sealed class EditorAction {
    data class AddTextBox(val textBox: TextBox) : EditorAction()
    data class DeleteTextBox(val textBox: TextBox) : EditorAction()
    data class UpdateTextBox(val oldTextBox: TextBox, val newTextBox: TextBox) : EditorAction()
}



sealed class MemeEditorAction {

    data object OnArrowBackClick : MemeEditorAction()

    // Default Editor actions
    data object Undo : MemeEditorAction()
    data object Redo : MemeEditorAction()
    data object AddTextBox : MemeEditorAction()
    data class SaveMeme(@DrawableRes val resId: Int) : MemeEditorAction()

    data object ToggleFont : MemeEditorAction()
    data class UpdateTextColor(val color: MemeTextColor) : MemeEditorAction()
    data class UpdateFontSize(val newFontSize: Float) : MemeEditorAction()

    data class StartEditingText(val textBox: TextBox) : MemeEditorAction()
    data class ConfirmTextChange(val newText: String) : MemeEditorAction()
    data object CancelEditing : MemeEditorAction()

    data class UpdateTextBoxPosition(val id: Int, val newPos: Offset) : MemeEditorAction()
    data class DeleteTextBox(val id: Int) : MemeEditorAction()
    data class SelectTextBox(val id: Int) : MemeEditorAction()
    data class UpdateImagePosition(val offset: Offset, val size: IntSize) : MemeEditorAction()
    data class UpdateFont(val font: MemeFont) : MemeEditorAction()

}

sealed interface MemeEditorEvent {
    data object OnNavigateBack: MemeEditorEvent
    data class OnSaveResult(val success: Boolean, val filePath: String? = null) : MemeEditorEvent
}

class MemeEditorViewModel(
    private val saveMemeUseCase: SaveMemeUseCase
) : ViewModel() {

    private var _state by mutableStateOf(MemeEditorState())
    val state: MemeEditorState get() = _state

    // Channel for one-time events
    private val eventChannel = Channel<MemeEditorEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    private var nextId = 0
    private val maxUndoSteps = 5

    fun onAction(action: MemeEditorAction) {
        when (action) {
            is MemeEditorAction.Redo -> performRedo()
            is MemeEditorAction.Undo -> performUndo()
            is MemeEditorAction.AddTextBox -> addTextBox()
            is MemeEditorAction.SaveMeme -> saveMeme(action.resId)
            is MemeEditorAction.ToggleFont -> toggleFont()
            is MemeEditorAction.UpdateFontSize -> setFontSize(action.newFontSize)
            is MemeEditorAction.UpdateTextColor -> setTextColor(action.color)
            is MemeEditorAction.StartEditingText -> startEditing(action.textBox)
            is MemeEditorAction.ConfirmTextChange -> confirmTextChange(action.newText)
            MemeEditorAction.CancelEditing -> cancelEditing()
            is MemeEditorAction.UpdateTextBoxPosition -> updateTextBoxPosition(action.id, action.newPos)
            is MemeEditorAction.DeleteTextBox -> deleteTextBox(action.id)
            is MemeEditorAction.SelectTextBox -> selectTextBox(action.id)
            is MemeEditorAction.UpdateImagePosition -> updateImagePosition(action.offset, action.size)
            is MemeEditorAction.OnArrowBackClick -> navigateBack()
            is MemeEditorAction.UpdateFont -> updateFont(action.font)

        }
    }

    private fun updateFont(newFont: MemeFont) {
        val selected = state.currentEditingTextBox ?: return
        val index = state.textBoxes.indexOfFirst { it.id == selected.id }
        if (index != -1) {
            val oldTextBox = state.textBoxes[index]
            val newTextBox = oldTextBox.copy(
                style = oldTextBox.style.copy(font = newFont)
            )

            val updated = state.textBoxes.toMutableList().apply {
                this[index] = newTextBox
            }

            addToUndoStack(EditorAction.UpdateTextBox(oldTextBox, newTextBox))

            _state = _state.copy(
                textBoxes = updated,
                currentEditingTextBox = newTextBox
            )
        }
    }

    private fun addTextBox() {
        if (state.imageSize.width != 0 && state.imageSize.height != 0) {
            val text = "TAP TWICE TO EDIT"
            val (initialPos, textBoxSize) = measureInitialTextBoxPosition(
                text = text,
                fontSizeSp = 36f,
                imageSize = state.imageSize
            )

            val nonOverlappingPosition = findNonOverlappingPosition(
                textBoxes = state.textBoxes,
                imageSize = state.imageSize,
                initialPosition = initialPos,
                approximateTextBoxSize = textBoxSize,
                maxAttempts = 20,
                offsetStep = 20f
            )

            val newBox = TextBox(
                id = nextId++,
                text = text,
                position = nonOverlappingPosition,
                style = MemeTextStyle(font = MemeFont.IMPACT, fontSize = 36f)
            )

            // Add to undo stack
            addToUndoStack(EditorAction.AddTextBox(newBox))

            _state = _state.copy(textBoxes = state.textBoxes + newBox)
        }
    }

    private fun toggleFont() {
        val selected = state.currentEditingTextBox ?: return
        val index = state.textBoxes.indexOfFirst { it.id == selected.id }
        if (index != -1) {
            val oldTextBox = state.textBoxes[index]
            val currentStyle = oldTextBox.style
            val newFont = if (currentStyle.font == MemeFont.IMPACT) {
                MemeFont.SYSTEM
            } else {
                MemeFont.IMPACT
            }

            val newTextBox = oldTextBox.copy(
                style = currentStyle.copy(font = newFont)
            )

            val updated = state.textBoxes.toMutableList().apply {
                this[index] = newTextBox
            }

            // Add to undo stack
            addToUndoStack(EditorAction.UpdateTextBox(oldTextBox, newTextBox))

            _state = _state.copy(
                textBoxes = updated,
                currentEditingTextBox = newTextBox
            )
        }
    }

    private fun setFontSize(newSize: Float) {
        val selected = state.currentEditingTextBox ?: return
        val index = state.textBoxes.indexOfFirst { it.id == selected.id }
        if (index != -1) {
            val oldTextBox = state.textBoxes[index]
            val newTextBox = oldTextBox.copy(
                style = oldTextBox.style.copy(fontSize = newSize)
            )

            val updated = state.textBoxes.toMutableList().apply {
                this[index] = newTextBox
            }

            addToUndoStack(EditorAction.UpdateTextBox(oldTextBox, newTextBox))

            _state = _state.copy(
                textBoxes = updated,
                currentEditingTextBox = newTextBox
            )
        }
    }

    private fun setTextColor(color: MemeTextColor) {
        val selected = state.currentEditingTextBox ?: return
        val index = state.textBoxes.indexOfFirst { it.id == selected.id }
        if (index != -1) {
            val oldTextBox = state.textBoxes[index]
            val newTextBox = oldTextBox.copy(
                style = oldTextBox.style.copy(color = color)
            )

            val updated = state.textBoxes.toMutableList().apply {
                this[index] = newTextBox
            }

            addToUndoStack(EditorAction.UpdateTextBox(oldTextBox, newTextBox))

            _state = _state.copy(
                textBoxes = updated,
                currentEditingTextBox = newTextBox
            )
        }
    }

    private fun startEditing(textBox: TextBox) {
        _state = _state.copy(currentEditingTextBox = textBox, showEditDialog = true)
    }

    private fun confirmTextChange(newText: String) {
        val editing = state.currentEditingTextBox ?: return
        val index = state.textBoxes.indexOfFirst { it.id == editing.id }
        if (index != -1) {
            val oldTextBox = state.textBoxes[index]
            val newTextBox = oldTextBox.copy(text = newText)
            val updated = state.textBoxes.toMutableList().apply {
                this[index] = newTextBox
            }

            addToUndoStack(EditorAction.UpdateTextBox(oldTextBox, newTextBox))

            _state = _state.copy(
                textBoxes = updated,
                currentEditingTextBox = null,
                showEditDialog = false
            )
        }
    }

    private fun deleteTextBox(id: Int) {
        val textBox = state.textBoxes.find { it.id == id } ?: return
        val updated = state.textBoxes.filter { it.id != id }
        val wasEditing = state.currentEditingTextBox?.id == id

        addToUndoStack(EditorAction.DeleteTextBox(textBox))

        _state = _state.copy(
            textBoxes = updated,
            currentEditingTextBox = if (wasEditing) null else state.currentEditingTextBox
        )
    }

    private fun updateTextBoxPosition(id: Int, newPos: Offset) {
        val index = state.textBoxes.indexOfFirst { it.id == id }
        if (index != -1) {
            val oldTextBox = state.textBoxes[index]
            val newTextBox = oldTextBox.copy(position = newPos)
            val updated = state.textBoxes.toMutableList().apply {
                this[index] = newTextBox
            }

            addToUndoStack(EditorAction.UpdateTextBox(oldTextBox, newTextBox))

            _state = _state.copy(
                textBoxes = updated,
                currentEditingTextBox = if (state.currentEditingTextBox?.id == id) newTextBox
                else state.currentEditingTextBox
            )
        }
    }

    private fun addToUndoStack(action: EditorAction) {
        _state = _state.copy(
            undoStack = (_state.undoStack + action).takeLast(maxUndoSteps),
            redoStack = emptyList() // Clear redo stack when new action is performed
        )
    }

    private fun performUndo() {
        val lastAction = state.undoStack.lastOrNull() ?: return

        val newState = when (lastAction) {
            is EditorAction.AddTextBox -> {
                _state.copy(
                    textBoxes = _state.textBoxes.filter { it.id != lastAction.textBox.id },
                    undoStack = _state.undoStack.dropLast(1),
                    redoStack = _state.redoStack + lastAction,
                    currentEditingTextBox = if (_state.currentEditingTextBox?.id == lastAction.textBox.id)
                        null else _state.currentEditingTextBox
                )
            }
            is EditorAction.DeleteTextBox -> {
                _state.copy(
                    textBoxes = _state.textBoxes + lastAction.textBox,
                    undoStack = _state.undoStack.dropLast(1),
                    redoStack = _state.redoStack + lastAction
                )
            }
            is EditorAction.UpdateTextBox -> {
                val updatedBoxes = _state.textBoxes.map {
                    if (it.id == lastAction.newTextBox.id) lastAction.oldTextBox else it
                }
                _state.copy(
                    textBoxes = updatedBoxes,
                    undoStack = _state.undoStack.dropLast(1),
                    redoStack = _state.redoStack + lastAction,
                    currentEditingTextBox = if (_state.currentEditingTextBox?.id == lastAction.newTextBox.id)
                        lastAction.oldTextBox else _state.currentEditingTextBox
                )
            }
        }
        _state = newState
    }

    private fun performRedo() {
        val lastAction = state.redoStack.lastOrNull() ?: return

        val newState = when (lastAction) {
            is EditorAction.AddTextBox -> {
                _state.copy(
                    textBoxes = _state.textBoxes + lastAction.textBox,
                    redoStack = _state.redoStack.dropLast(1),
                    undoStack = _state.undoStack + lastAction
                )
            }
            is EditorAction.DeleteTextBox -> {
                _state.copy(
                    textBoxes = _state.textBoxes.filter { it.id != lastAction.textBox.id },
                    redoStack = _state.redoStack.dropLast(1),
                    undoStack = _state.undoStack + lastAction,
                    currentEditingTextBox = if (_state.currentEditingTextBox?.id == lastAction.textBox.id)
                        null else _state.currentEditingTextBox
                )
            }
            is EditorAction.UpdateTextBox -> {
                val updatedBoxes = _state.textBoxes.map {
                    if (it.id == lastAction.oldTextBox.id) lastAction.newTextBox else it
                }
                _state.copy(
                    textBoxes = updatedBoxes,
                    redoStack = _state.redoStack.dropLast(1),
                    undoStack = _state.undoStack + lastAction,
                    currentEditingTextBox = if (_state.currentEditingTextBox?.id == lastAction.oldTextBox.id)
                        lastAction.newTextBox else _state.currentEditingTextBox
                )
            }
        }
        _state = newState
    }

    private fun cancelEditing() {
        _state = _state.copy(currentEditingTextBox = null, showEditDialog = false)
    }

    private fun selectTextBox(id: Int) {
        val selected = state.textBoxes.find { it.id == id }
        _state = _state.copy(currentEditingTextBox = selected)
    }

    private fun updateImagePosition(offset: Offset, size: IntSize) {
        _state = _state.copy(imageOffset = offset, imageSize = size)
    }

    private fun navigateBack() {
        viewModelScope.launch {
            eventChannel.send(MemeEditorEvent.OnNavigateBack)
        }
    }

    private fun saveMeme(resId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            saveMemeUseCase.saveMeme(
                backgroundImageResId = resId,
                textBoxes = state.textBoxes,
                imageWidth = state.imageSize.width,
                imageHeight = state.imageSize.height
            ).onSuccess { path ->
                eventChannel.send(MemeEditorEvent.OnSaveResult(success = true, filePath = path))
            }.onFailure { _ ->
                eventChannel.send(MemeEditorEvent.OnSaveResult(success = false))
            }
        }
    }
}

private fun measureInitialTextBoxPosition(
    text: String,
    fontSizeSp: Float,
    imageSize: IntSize
): Pair<Offset, androidx.compose.ui.geometry.Size> {
    val approximateWidth = fontSizeSp * text.length / 2f
    val approximateHeight = fontSizeSp * 2f

    val initialCenterX = (imageSize.width - approximateWidth) / 2f
    val initialCenterY = (imageSize.height - approximateHeight) / 2f

    return Offset(initialCenterX, initialCenterY) to androidx.compose.ui.geometry.Size(
        approximateWidth,
        approximateHeight
    )
}

private fun findNonOverlappingPosition(
    textBoxes: List<TextBox>,
    imageSize: IntSize,
    initialPosition: Offset,
    approximateTextBoxSize: androidx.compose.ui.geometry.Size = androidx.compose.ui.geometry.Size(
        200f,
        50f
    ),
    maxAttempts: Int = 20,
    offsetStep: Float = 20f
): Offset {
    var position = initialPosition
    var attempts = 0

    while (attempts < maxAttempts) {
        val isOverlapping = textBoxes.any { existingTextBox ->
            val distance = (existingTextBox.position - position).getDistance()
            distance < (approximateTextBoxSize.width / 2)
        }
        if (!isOverlapping) {
            return position
        }

        position = position.copy(
            x = (position.x + offsetStep).coerceAtMost(imageSize.width - approximateTextBoxSize.width),
            y = (position.y + offsetStep).coerceAtMost(imageSize.height - approximateTextBoxSize.height)
        )
        attempts++
    }
    return position
}
