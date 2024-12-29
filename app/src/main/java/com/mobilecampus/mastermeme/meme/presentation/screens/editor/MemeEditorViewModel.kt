package com.mobilecampus.mastermeme.meme.presentation.screens.editor

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
import com.mobilecampus.mastermeme.meme.domain.use_case.ShareMemesUseCase
import com.mobilecampus.mastermeme.meme.domain.use_case.ShareTemporaryMeme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class CurrentlyEditedTextBox(
    val currentTextBox: TextBox,
    val currentTextBoxBeforeChanges: TextBox? = null,
    val isNew: Boolean
)

data class MemeEditorState(
    val textBoxes: List<TextBox> = emptyList(),
    val currentlyEditedTextBox: CurrentlyEditedTextBox? = null,
    val showEditDialog: Boolean = false,
    val showDiscardChangesConfirmationDialog: Boolean = false,
    val imageOffset: Offset = Offset.Zero,
    val imageSize: IntSize = IntSize.Zero,
    val shouldShowBottomSheet: Boolean = false,
    val undoStack: List<UndoRedoAction> = emptyList(),
    val redoStack: List<UndoRedoAction> = emptyList(),
    val editingTextBoxId: Int? = null, // New property to track which text box is being edited
    val isInEditMode: Boolean = false   // New property to track if we're in edit mode
)

sealed class UndoRedoAction {
    data class AddTextBoxToUndoStack(val textBox: TextBox) : UndoRedoAction()
    data class DeleteTextBoxFromUndoStack(val textBox: TextBox) : UndoRedoAction()
    data class UpdateTextBoxInUndoStack(val oldTextBox: TextBox, val newTextBox: TextBox) :
        UndoRedoAction()
}

sealed interface MemeEditorAction {

    data object OnArrowBackClick : MemeEditorAction
    data object NavigateBackDiscardingChanges : MemeEditorAction

    // Default Editor actions
    data object Undo : MemeEditorAction
    data object Redo : MemeEditorAction
    data object AddTextBox : MemeEditorAction
    data class SaveMeme(@DrawableRes val resId: Int) : MemeEditorAction

    data object ToggleFont : MemeEditorAction
    data class UpdateTextColor(val color: MemeTextColor) : MemeEditorAction
    data class UpdateFontSize(val newFontSize: Float) : MemeEditorAction

    data object ShowEditTextDialog : MemeEditorAction
    data class ShowDiscardChangesConfirmationDialog(val isDisplay: Boolean) : MemeEditorAction
    data class UpdateText(val newText: String? = null) : MemeEditorAction
    data object ConfirmEditing : MemeEditorAction
    data object CancelEditing : MemeEditorAction

    data class UpdateTextBoxPosition(val id: Int, val newPos: Offset) : MemeEditorAction
    data class DeleteTextBox(val id: Int) : MemeEditorAction
    data class SelectTextBox(val textBox: TextBox) : MemeEditorAction
    data class UpdateImagePosition(val offset: Offset, val size: IntSize) : MemeEditorAction
    data class UpdateFont(val font: MemeFont) : MemeEditorAction

    data object ShowBottomSheet : MemeEditorAction
    data object HideBottomSheet : MemeEditorAction

    data class ShareMeme(@DrawableRes val resId: Int) : MemeEditorAction

    data class EnterEditMode(val textBoxId: Int) : MemeEditorAction
    data object ExitEditMode : MemeEditorAction
    data class UpdateEditingText(val newText: String) : MemeEditorAction



}

sealed interface MemeEditorEvent {
    data object OnNavigateBack : MemeEditorEvent
    data class OnSaveResult(val success: Boolean, val filePath: String? = null) : MemeEditorEvent
}

class MemeEditorViewModel(
    private val saveMemeUseCase: SaveMemeUseCase,
    private val shareMemeUseCase: ShareMemesUseCase,
    private val shareTemporaryMeme: ShareTemporaryMeme
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
            is MemeEditorAction.ShowEditTextDialog -> showEditTextDialog()
            is MemeEditorAction.ShowDiscardChangesConfirmationDialog -> showDiscardChangesConfirmationDialog(action.isDisplay)
            is MemeEditorAction.UpdateText -> updateText(action.newText)
            is MemeEditorAction.CancelEditing -> cancelEditing()
            is MemeEditorAction.ConfirmEditing -> confirmEditing()
            is MemeEditorAction.UpdateTextBoxPosition -> updateTextBoxPosition(action.id, action.newPos)
            is MemeEditorAction.DeleteTextBox -> deleteTextBox(action.id)
            is MemeEditorAction.SelectTextBox -> selectTextBox(action.textBox)
            is MemeEditorAction.UpdateImagePosition -> updateImagePosition(action.offset, action.size)
            is MemeEditorAction.OnArrowBackClick -> checkNavigateBack()
            is MemeEditorAction.NavigateBackDiscardingChanges -> forceNavigateBack()
            is MemeEditorAction.UpdateFont -> updateFont(action.font)
            is MemeEditorAction.ShowBottomSheet -> handleBottomSheetVisibility(true)
            is MemeEditorAction.HideBottomSheet -> handleBottomSheetVisibility(false)
            is MemeEditorAction.ShareMeme -> shareMeme(action.resId)
            is MemeEditorAction.EnterEditMode -> enterEditMode(action.textBoxId)
            is MemeEditorAction.ExitEditMode -> exitEditMode()
            is MemeEditorAction.UpdateEditingText -> updateEditingText(action.newText)

        }
    }

    private fun forceNavigateBack(){
        viewModelScope.launch {
            eventChannel.send(MemeEditorEvent.OnNavigateBack)
        }
    }

    private fun enterEditMode(textBoxId: Int) {
        _state = _state.copy(
            editingTextBoxId = textBoxId,
            isInEditMode = true
        )
    }

    private fun exitEditMode() {
        _state = _state.copy(
            editingTextBoxId = null,
            isInEditMode = false
        )
    }

    private fun updateEditingText(newText: String) {
        val editingId = state.editingTextBoxId ?: return

        val updatedBoxes = state.textBoxes.map { textBox ->
            if (textBox.id == editingId) {
                textBox.copy(text = newText)
            } else {
                textBox
            }
        }

        _state = _state.copy(
            textBoxes = updatedBoxes,
            currentlyEditedTextBox = state.currentlyEditedTextBox?.let { edited ->
                if (edited.currentTextBox.id == editingId) {
                    edited.copy(currentTextBox = edited.currentTextBox.copy(text = newText))
                } else {
                    edited
                }
            }
        )
    }

    private fun shareMeme(resId: Int) {
        viewModelScope.launch {
            shareTemporaryMeme(
                backgroundImageResId = resId,
                textBoxes = _state.textBoxes,
                imageWidth = _state.imageSize.width,
                imageHeight = _state.imageSize.height
            )
        }
    }

    private fun handleBottomSheetVisibility(shouldShowBottomSheet: Boolean) {
        viewModelScope.launch {
            _state = _state.copy(shouldShowBottomSheet = shouldShowBottomSheet)
        }
    }

    private fun updateFont(newFont: MemeFont) {
        state.currentlyEditedTextBox?.let { edited ->
            val newTextBox = edited.currentTextBox.copy(
                style = edited.currentTextBox.style.copy(font = newFont)
            )
            updateCurrentlyEditedTextBox(newTextBox)
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

            // Get style from last edited text box or use default
            val style = state.currentlyEditedTextBox?.currentTextBox?.style ?:
            MemeTextStyle(font = MemeFont.IMPACT, fontSize = 36f)

            val newBox = TextBox(
                id = nextId++,
                text = text,
                position = nonOverlappingPosition,
                style = style.copy() // Use copy to avoid reference issues
            )

            _state = _state.copy(
                textBoxes = state.textBoxes + newBox,
                currentlyEditedTextBox = CurrentlyEditedTextBox(
                    currentTextBox = newBox,
                    currentTextBoxBeforeChanges = newBox.copy(),
                    isNew = true
                )
            )
        }
    }

    private fun toggleFont() {
        state.currentlyEditedTextBox?.let { edited ->
            val newFont = if (edited.currentTextBox.style.font == MemeFont.IMPACT) {
                MemeFont.SYSTEM
            } else {
                MemeFont.IMPACT
            }

            val newTextBox = edited.currentTextBox.copy(
                style = edited.currentTextBox.style.copy(font = newFont)
            )

            updateCurrentlyEditedTextBox(newTextBox)
        }
    }

    private fun setFontSize(newSize: Float) {
        state.currentlyEditedTextBox?.let { edited ->
            val newTextBox = edited.currentTextBox.copy(
                style = edited.currentTextBox.style.copy(fontSize = newSize)
            )
            updateCurrentlyEditedTextBox(newTextBox)
        }
    }


    private fun setTextColor(color: MemeTextColor) {
        state.currentlyEditedTextBox?.let { edited ->
            val newTextBox = edited.currentTextBox.copy(
                style = edited.currentTextBox.style.copy(color = color)
            )
            updateCurrentlyEditedTextBox(newTextBox)
        }
    }

    private fun updateCurrentlyEditedTextBox(newTextBox: TextBox) {
        state.currentlyEditedTextBox?.let { edited ->
            _state = _state.copy(
                textBoxes = state.textBoxes.map {
                    if (it.id == edited.currentTextBox.id) newTextBox else it
                },
                currentlyEditedTextBox = edited.copy(
                    currentTextBox = newTextBox,
                    // Keep the original before-changes text box
                    currentTextBoxBeforeChanges = edited.currentTextBoxBeforeChanges
                )
            )
        }
    }

    private fun showEditTextDialog() {
        _state = _state.copy(showEditDialog = true)
    }

    private fun showDiscardChangesConfirmationDialog(isDisplay: Boolean) {
        _state = _state.copy(showDiscardChangesConfirmationDialog = isDisplay)
    }

    private fun updateText(newText: String?) {
        state.currentlyEditedTextBox?.let { edited ->
            newText?.let { text ->
                val newTextBox = edited.currentTextBox.copy(text = text)
                _state = _state.copy(
                    textBoxes = state.textBoxes.map {
                        if (it.id == edited.currentTextBox.id) newTextBox else it
                    },
                    currentlyEditedTextBox = edited.copy(currentTextBox = newTextBox),
                    showEditDialog = false
                )
            } ?: run {
                _state = _state.copy(
                    showEditDialog = false
                )
            }
        }
    }

    private fun deleteTextBox(id: Int, useUndo: Boolean = true) {
        val textBox = state.textBoxes.find { it.id == id } ?: return
        val updated = state.textBoxes.filter { it.id != id }
        val wasEditing = state.currentlyEditedTextBox?.currentTextBox?.id == id

        if (useUndo) {
            addToUndoStack(UndoRedoAction.DeleteTextBoxFromUndoStack(textBox))
        }

        _state = _state.copy(
            textBoxes = updated,
            currentlyEditedTextBox = if (wasEditing) null else state.currentlyEditedTextBox
        )
    }

    private fun updateTextBoxPosition(id: Int, newPos: Offset) {
        state.textBoxes.find { it.id == id }?.let { textBox ->
            val newTextBox = textBox.copy(position = newPos)

            _state = _state.copy(
                textBoxes = state.textBoxes.map {
                    if (it.id == id) newTextBox else it
                },
                currentlyEditedTextBox = state.currentlyEditedTextBox?.let { edited ->
                    if (edited.currentTextBox.id == id) {
                        edited.copy(currentTextBox = newTextBox)
                    } else edited
                }
            )
        }
    }

    private fun addToUndoStack(action: UndoRedoAction) {
        _state = _state.copy(
            undoStack = (_state.undoStack + action).takeLast(maxUndoSteps),
            redoStack = emptyList() // Clear redo stack when new action is performed
        )
    }

    private fun performUndo() {
        val lastAction = state.undoStack.lastOrNull() ?: return

        val newState = when (lastAction) {
            is UndoRedoAction.AddTextBoxToUndoStack -> {
                _state.copy(
                    textBoxes = _state.textBoxes.filter { it.id != lastAction.textBox.id },
                    undoStack = _state.undoStack.dropLast(1),
                    redoStack = _state.redoStack + lastAction,
                    currentlyEditedTextBox = if (_state.currentlyEditedTextBox?.currentTextBox?.id == lastAction.textBox.id)
                        null else _state.currentlyEditedTextBox
                )
            }

            is UndoRedoAction.DeleteTextBoxFromUndoStack -> {
                _state.copy(
                    textBoxes = _state.textBoxes + lastAction.textBox,
                    undoStack = _state.undoStack.dropLast(1),
                    redoStack = _state.redoStack + lastAction
                )
            }

            is UndoRedoAction.UpdateTextBoxInUndoStack -> {
                val updatedBoxes = _state.textBoxes.map {
                    if (it.id == lastAction.newTextBox.id) lastAction.oldTextBox else it
                }
                _state.copy(
                    textBoxes = updatedBoxes,
                    undoStack = _state.undoStack.dropLast(1),
                    redoStack = _state.redoStack + lastAction,
                    currentlyEditedTextBox = if (_state.currentlyEditedTextBox?.currentTextBox?.id == lastAction.newTextBox.id)
                        CurrentlyEditedTextBox(
                            currentTextBox = lastAction.oldTextBox,
                            currentTextBoxBeforeChanges = lastAction.oldTextBox,
                            isNew = false
                        ) else _state.currentlyEditedTextBox
                )
            }
        }
        _state = newState
    }

    private fun performRedo() {
        val lastAction = state.redoStack.lastOrNull() ?: return

        val newState = when (lastAction) {
            is UndoRedoAction.AddTextBoxToUndoStack -> {
                _state.copy(
                    textBoxes = _state.textBoxes + lastAction.textBox,
                    redoStack = _state.redoStack.dropLast(1),
                    undoStack = _state.undoStack + lastAction
                )
            }

            is UndoRedoAction.DeleteTextBoxFromUndoStack -> {
                _state.copy(
                    textBoxes = _state.textBoxes.filter { it.id != lastAction.textBox.id },
                    redoStack = _state.redoStack.dropLast(1),
                    undoStack = _state.undoStack + lastAction,
                    currentlyEditedTextBox = if (_state.currentlyEditedTextBox?.currentTextBox?.id == lastAction.textBox.id)
                        null else _state.currentlyEditedTextBox
                )
            }

            is UndoRedoAction.UpdateTextBoxInUndoStack -> {
                val updatedBoxes = _state.textBoxes.map {
                    if (it.id == lastAction.oldTextBox.id) lastAction.newTextBox else it
                }
                _state.copy(
                    textBoxes = updatedBoxes,
                    redoStack = _state.redoStack.dropLast(1),
                    undoStack = _state.undoStack + lastAction,
                    currentlyEditedTextBox = if (_state.currentlyEditedTextBox?.currentTextBox?.id == lastAction.oldTextBox.id)
                        CurrentlyEditedTextBox(
                            currentTextBox = lastAction.newTextBox,
                            currentTextBoxBeforeChanges = lastAction.newTextBox,
                            isNew = false
                        ) else _state.currentlyEditedTextBox
                )
            }
        }
        _state = newState
    }

    private fun cancelEditing() {
        state.currentlyEditedTextBox?.let { edited ->
            val index = state.textBoxes.indexOfFirst { it.id == edited.currentTextBox.id }
            if (index != -1) {
                edited.currentTextBoxBeforeChanges?.let { textBoxBeforeChanges ->
                    val updated = state.textBoxes.toMutableList().apply {
                        this[index] = textBoxBeforeChanges
                    }

                    if (edited.isNew) {
                        deleteTextBox(edited.currentTextBox.id, useUndo = false)
                        return
                    } else {
                        _state = _state.copy(textBoxes = updated)
                    }
                }
            }
        }

        _state = _state.copy(currentlyEditedTextBox = null, showEditDialog = false)
    }

    private fun confirmEditing() {
        state.currentlyEditedTextBox?.let { edited ->
            val index = state.textBoxes.indexOfFirst { it.id == edited.currentTextBox.id }
            if (index != -1) {
                edited.currentTextBoxBeforeChanges?.let { textBoxBeforeChanges ->
                    if (edited.isNew) {
                        addToUndoStack(UndoRedoAction.AddTextBoxToUndoStack(edited.currentTextBox))
                    } else {
                        addToUndoStack(
                            UndoRedoAction.UpdateTextBoxInUndoStack(
                                textBoxBeforeChanges,
                                edited.currentTextBox
                            )
                        )
                    }
                }
            }
        }

        _state = _state.copy(currentlyEditedTextBox = null, showEditDialog = false)
    }

    private fun selectTextBox(textBox: TextBox) {
        // If we're selecting the same text box, don't reset anything
        if (state.currentlyEditedTextBox?.currentTextBox?.id == textBox.id) {
            return
        }

        // If we were editing another text box, save its state first
        state.currentlyEditedTextBox?.let { edited ->
            if (edited.currentTextBox != edited.currentTextBoxBeforeChanges) {
                addToUndoStack(
                    UndoRedoAction.UpdateTextBoxInUndoStack(
                        edited.currentTextBoxBeforeChanges ?: edited.currentTextBox,
                        edited.currentTextBox
                    )
                )
            }
        }

        // Find the text box in our current list to get its latest state
        val currentTextBox = state.textBoxes.find { it.id == textBox.id } ?: textBox

        _state = _state.copy(
            currentlyEditedTextBox = CurrentlyEditedTextBox(
                currentTextBox = currentTextBox,
                currentTextBoxBeforeChanges = currentTextBox.copy(),
                isNew = false
            )
        )
    }

    private fun updateImagePosition(offset: Offset, size: IntSize) {
        _state = _state.copy(imageOffset = offset, imageSize = size)
    }

    private fun checkNavigateBack() {
        viewModelScope.launch {
            if (state.redoStack.isNotEmpty() || state.undoStack.isNotEmpty()) {
                // Show confirmation dialog if there are unsaved changes
                onAction(MemeEditorAction.ShowDiscardChangesConfirmationDialog(isDisplay = true))
            } else {
                // Navigate back directly if there are no changes
                eventChannel.send(MemeEditorEvent.OnNavigateBack)
            }
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
    val imageWidth = imageSize.width.toFloat()
    val imageHeight = imageSize.height.toFloat()

    // Calculate text dimensions
    val approximateWidth = fontSizeSp * text.length * 0.4f
    val approximateHeight = fontSizeSp * 1.5f

    // Calculate the text offset to account for coordinate space differences
    val textOffset = imageWidth * 0.273f  // 350f / 1280f ≈ 0.273

    // Calculate centered position with coordinate adjustment
    val centerX = imageWidth / 2f - approximateWidth / 2f - textOffset
    val centerY = imageHeight / 2f - approximateHeight / 2f

    return Offset(centerX, centerY) to androidx.compose.ui.geometry.Size(
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
