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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class MemeEditorState(
    val textBoxes: List<TextBox> = emptyList(),
    val currentEditingTextBox: TextBox? = null,
    val showEditDialog: Boolean = false,
    val imageOffset: Offset = Offset.Zero,
    val imageSize: IntSize = IntSize.Zero
)

sealed class MemeEditorAction {
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
}

sealed interface MemeEditorEvent {
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

    fun onAction(event: MemeEditorAction) {
        when (event) {
            is MemeEditorAction.Redo -> {} // TODO
            is MemeEditorAction.Undo -> {} // TODO
            is MemeEditorAction.AddTextBox -> addTextBox()
            is MemeEditorAction.SaveMeme -> saveMeme(event.resId)

            is MemeEditorAction.ToggleFont -> toggleFont()
            is MemeEditorAction.UpdateFontSize -> setFontSize(event.newFontSize)
            is MemeEditorAction.UpdateTextColor -> setTextColor(event.color)

            is MemeEditorAction.StartEditingText -> startEditing(event.textBox)
            is MemeEditorAction.ConfirmTextChange -> confirmTextChange(event.newText)
            MemeEditorAction.CancelEditing -> cancelEditing()

            is MemeEditorAction.UpdateTextBoxPosition -> updateTextBoxPosition(
                event.id,
                event.newPos
            )

            is MemeEditorAction.DeleteTextBox -> deleteTextBox(event.id)
            is MemeEditorAction.SelectTextBox -> selectTextBox(event.id)
            is MemeEditorAction.UpdateImagePosition -> updateImagePosition(event.offset, event.size)
        }
    }

    private fun addTextBox() {
        if (state.imageSize.width != 0 && state.imageSize.height != 0) {
            val text = "TAP TWICE TO EDIT"
            val (initialPos, textBoxSize) = measureInitialTextBoxPosition(
                text,
                36f,
                state.imageSize
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
            _state = _state.copy(textBoxes = state.textBoxes + newBox)
        }
    }

    private fun toggleFont() {
        val selected = state.currentEditingTextBox ?: return
        val index = state.textBoxes.indexOfFirst { it.id == selected.id }
        if (index != -1) {
            val currentStyle = state.textBoxes[index].style
            val newFont =
                if (currentStyle.font == MemeFont.IMPACT) MemeFont.SYSTEM else MemeFont.IMPACT
            val updated = state.textBoxes.toMutableList().apply {
                this[index] = this[index].copy(style = currentStyle.copy(font = newFont))
            }
            _state = _state.copy(textBoxes = updated)
        }
    }

    private fun setFontSize(newSize: Float) {
        val selected = state.currentEditingTextBox ?: return
        val index = state.textBoxes.indexOfFirst { it.id == selected.id }
        if (index != -1) {
            val updated = state.textBoxes.toMutableList().apply {
                this[index] = this[index].copy(style = this[index].style.copy(fontSize = newSize))
            }
            _state = _state.copy(textBoxes = updated)
        }
    }

    private fun setTextColor(color: MemeTextColor) {
        val selected = state.currentEditingTextBox ?: return
        val index = state.textBoxes.indexOfFirst { it.id == selected.id }
        if (index != -1) {
            val updated = state.textBoxes.toMutableList().apply {
                this[index] = this[index].copy(style = this[index].style.copy(color = color))
            }
            _state = _state.copy(textBoxes = updated)
        }
    }

    private fun startEditing(textBox: TextBox) {
        _state = _state.copy(currentEditingTextBox = textBox, showEditDialog = true)
    }

    private fun confirmTextChange(newText: String) {
        val editing = state.currentEditingTextBox ?: return
        val index = state.textBoxes.indexOfFirst { it.id == editing.id }
        if (index != -1) {
            val updated = state.textBoxes.toMutableList()
            updated[index] = updated[index].copy(text = newText)
            _state = _state.copy(
                textBoxes = updated,
                currentEditingTextBox = null,
                showEditDialog = false
            )
        }
    }

    private fun cancelEditing() {
        _state = _state.copy(currentEditingTextBox = null, showEditDialog = false)
    }

    private fun updateTextBoxPosition(id: Int, newPos: Offset) {
        val index = state.textBoxes.indexOfFirst { it.id == id }
        if (index != -1) {
            val updated = state.textBoxes.toMutableList()
            updated[index] = updated[index].copy(position = newPos)
            _state = _state.copy(textBoxes = updated)
        }
    }

    private fun deleteTextBox(id: Int) {
        val updated = state.textBoxes.filter { it.id != id }
        val wasEditing = state.currentEditingTextBox?.id == id
        _state = _state.copy(
            textBoxes = updated,
            currentEditingTextBox = if (wasEditing) null else state.currentEditingTextBox
        )
    }

    private fun selectTextBox(id: Int) {
        val selected = state.textBoxes.find { it.id == id }
        _state = _state.copy(currentEditingTextBox = selected)
    }

    private fun updateImagePosition(offset: Offset, size: IntSize) {
        _state = _state.copy(imageOffset = offset, imageSize = size)
    }

    private fun saveMeme(resId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            saveMemeUseCase.saveMeme(
                backgroundImageResId = resId,
                textBoxes = state.textBoxes,
                imageOffsetX = state.imageOffset.x,
                imageOffsetY = state.imageOffset.y,
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
