package com.mobilecampus.mastermeme.meme.presentation.screens.editor.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.mobilecampus.mastermeme.meme.domain.model.editor.TextBox
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DraggableTextBox(
    textBox: TextBox,
    imageOffset: Offset,
    imageSize: IntSize,
    onPositionChanged: (Offset) -> Unit,
    onDelete: () -> Unit,
    onDoubleClick: () -> Unit,
    onSelect: () -> Unit,
    isSelected: Boolean = false
) {
    var isDragging by remember { mutableStateOf(false) }
    var offsetX by remember(textBox.id, textBox.position.x) {
        mutableFloatStateOf(textBox.position.x)
    }
    var offsetY by remember(textBox.id, textBox.position.y) {
        mutableFloatStateOf(textBox.position.y)
    }

    val density = LocalDensity.current

    var textBoxWidth by remember { mutableFloatStateOf(0f) }
    var textBoxHeight by remember { mutableFloatStateOf(0f) }

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
            }
            .pointerInput(textBox.id) {
                detectDragGestures(
                    onDragStart = {
                        isDragging = true
                        onSelect()
                    },
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
            .combinedClickable(
                onDoubleClick = onDoubleClick,
                onClick = { onSelect() }
            )
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = if (isDragging) Color.LightGray.copy(alpha = 0.5f)
                    else Color.Transparent,
                    shape = RoundedCornerShape(4.dp)
                )
                .border(
                    width = 1.dp,
                    color = if (isSelected) Color.Yellow else Color.White,
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            OutlinedText(
                text = textBox.text,
                style = textBox.style,
                outlineWidth = with(density) { 4.dp.toPx() },
                paddingHorizontal = 4.dp,
                paddingVertical = 0.dp
            )
        }

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
