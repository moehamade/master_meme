package com.mobilecampus.mastermeme.meme.presentation.screens.editor.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobilecampus.mastermeme.meme.domain.model.editor.MemeFont
import com.mobilecampus.mastermeme.meme.domain.model.editor.TextBox
import com.mobilecampus.mastermeme.ui.theme.MasterMemeTheme
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun DraggableTextBox(
    textBox: TextBox,
    imageOffset: Offset,
    imageSize: IntSize,
    onPositionChanged: (Offset) -> Unit,
    onDelete: () -> Unit,
    onSelect: () -> Unit,
    onDoubleClick: () -> Unit,
    onTextChange: (String) -> Unit,
    onEditingComplete: () -> Unit,
    isSelected: Boolean,
    isEditing: Boolean
) {
    var isDragging by remember { mutableStateOf(false) }
    var offsetX by remember { mutableFloatStateOf(textBox.position.x) }
    var offsetY by remember { mutableFloatStateOf(textBox.position.y) }
    val density = LocalDensity.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    var textBoxWidth by remember { mutableFloatStateOf(0f) }
    var textBoxHeight by remember { mutableFloatStateOf(0f) }
    var textBounds by remember { mutableStateOf(Rect.Zero) }

    var textFieldValue by remember(textBox.text) {
        mutableStateOf(TextFieldValue(
            text = textBox.text,
            selection = TextRange(textBox.text.length)
        ))
    }

    fun completeEditing() {
        keyboardController?.hide()
        focusManager.clearFocus()
        onEditingComplete()
    }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    (imageOffset.x + offsetX).roundToInt(),
                    (imageOffset.y + offsetY).roundToInt()
                )
            }
            .onSizeChanged { size ->
                textBoxWidth = size.width.toFloat()
                textBoxHeight = size.height.toFloat()
                textBounds = Rect(0f, 0f, size.width.toFloat(), size.height.toFloat())
            }
            .pointerInput(isSelected, isEditing) {
                // Handle taps and check boundaries
                detectTapGestures(
                    onTap = { offset ->
                        if (!isEditing) {
                            onSelect()
                        } else if (!textBounds.contains(offset)) {
                            // Only exit edit mode if tap is outside text boundaries
                            completeEditing()
                        }
                        // If we're editing and tap is inside boundaries, do nothing to allow cursor positioning
                    },
                    onDoubleTap = { onDoubleClick() }
                )
            }
            .pointerInput(isSelected, isEditing) {
                if (isSelected && !isEditing) {
                    detectDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = { isDragging = false },
                        onDragCancel = { isDragging = false },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val maxX = imageSize.width.toFloat() - textBoxWidth
                            val maxY = imageSize.height.toFloat() - textBoxHeight
                            offsetX = (offsetX + dragAmount.x).coerceIn(0f, maxX)
                            offsetY = (offsetY + dragAmount.y).coerceIn(0f, maxY)
                            onPositionChanged(Offset(offsetX, offsetY))
                        }
                    )
                }
            }
    ) {
        Box(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = if (isSelected) Color.White else Color.Transparent,
                    shape = RoundedCornerShape(4.dp)
                )
                .background(
                    color = if (isDragging) Color.LightGray.copy(alpha = 0.5f)
                    else Color.Transparent,
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            if (isEditing) {
                BasicTextField(
                    value = textFieldValue,
                    onValueChange = { newValue ->
                        textFieldValue = newValue
                        onTextChange(newValue.text)
                    },
                    textStyle = TextStyle(
                        fontSize = textBox.style.fontSize.sp,
                        color = textBox.style.color.toFillColor(),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .padding(4.dp),
                    cursorBrush = SolidColor(Color.White),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { completeEditing() }
                    ),
                    singleLine = false
                )

                LaunchedEffect(isEditing) {
                    if (isEditing) {
                        focusRequester.requestFocus()
                        delay(100)
                        keyboardController?.show()
                    }
                }
            } else {
                OutlinedText(
                    text = textBox.text,
                    style = textBox.style,
                    outlineWidth = with(density) { 4.dp.toPx() },
                    paddingHorizontal = 4.dp,
                    paddingVertical = 0.dp
                )
            }
        }

        if (isSelected && !isEditing) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 12.dp, y = (-12).dp)
                    .clip(CircleShape)
                    .background(Color(0xFFB3261E))
                    .clickable { onDelete() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

//@Preview(showSystemUi = true, showBackground = true)
//@Composable
//private fun DraggableTextBoxPreview() {
//    MasterMemeTheme {
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .wrapContentSize(Alignment.Center)
//        ) {
//            DraggableTextBox(
//                textBox = TextBox(id = 0, text = "text"),
//                imageOffset = Offset(0f, 0f),
//                imageSize = IntSize(20, 20),
//            )
//        }
//    }
//}
