package com.mobilecampus.mastermeme.meme.presentation.screens.editor.components

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobilecampus.mastermeme.R
import com.mobilecampus.mastermeme.core.presentation.design_system.MasterMemeBackground
import com.mobilecampus.mastermeme.meme.domain.model.editor.MemeFont
import com.mobilecampus.mastermeme.meme.domain.model.editor.MemeTextColor
import com.mobilecampus.mastermeme.ui.theme.ExtendedTheme

sealed class BottomBarLayout {
    object Default : BottomBarLayout()
    object TextEditor : BottomBarLayout()
}

sealed class TextEditorOption {
    object FontFamily : TextEditorOption()
    object FontSize : TextEditorOption()
    object ColorPicker : TextEditorOption()
}

@Composable
fun MemeEditorBottomBar(
    currentLayout: BottomBarLayout,
    selectedColor: Color,
    selectedFont: MemeFont = MemeFont.IMPACT,  // Change this parameter
    selectedFontSize: Float = 16f,
    canUndo: Boolean,
    canRedo: Boolean,
    modifier: Modifier = Modifier,
    onUndo: () -> Unit = {},
    onRedo: () -> Unit = {},
    onAddTextBox: () -> Unit = {},
    onSaveMeme: () -> Unit = {},
    onCancelTextBoxEditing: () -> Unit = {},
    onConfirmTextBoxEditing: () -> Unit = {},
    onFontSelected: (MemeFont) -> Unit = {},  // Update this callback
    onFontSizeChanged: (Float) -> Unit = {},
    onColorSelected: (Color) -> Unit = {}
) {
    var selectedOption by remember { mutableStateOf<TextEditorOption?>(null) }

    Column(modifier = modifier) {
        // Optional content above the BottomAppBar based on selection
        AnimatedVisibility(
            visible = currentLayout == BottomBarLayout.TextEditor && selectedOption != null,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                tonalElevation = 2.dp
            ) {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    when (selectedOption) {
                        TextEditorOption.FontFamily -> FontFamilySelector(
                            selectedFont = selectedFont,
                            onSelected =  onFontSelected
                        )

                        TextEditorOption.FontSize -> FontSizeSelector(
                            selectedFontSize = selectedFontSize,
                            onSizeChanged = onFontSizeChanged
                        )

                        TextEditorOption.ColorPicker -> ColorSelector(
                            selectedColor = selectedColor,
                            onColorSelected = onColorSelected
                        )

                        null -> {}
                    }
                }
            }
        }

        BottomAppBar(
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            contentColor = MaterialTheme.colorScheme.onSurface,
            windowInsets = WindowInsets(
                left = 16.dp,
                right = 16.dp,
                bottom = 4.dp,
                top = 4.dp
            ),
            tonalElevation = 0.dp
        ) {
            when (currentLayout) {
                BottomBarLayout.Default -> DefaultBottomBarContent(
                    canUndo = canUndo,
                    canRedo = canRedo,
                    onUndo = onUndo,
                    onRedo = onRedo,
                    onAddTextBox = onAddTextBox,
                    onSaveMeme = onSaveMeme
                )

                BottomBarLayout.TextEditor -> TextEditorBottomBarContent(
                    selectedOption = selectedOption,
                    onOptionSelected = { option ->
                        selectedOption = if (selectedOption == option) null else option
                    },
                    onDismiss = {
                        selectedOption = null
                        onCancelTextBoxEditing()
                    },
                    onConfirm = {
                        selectedOption = null
                        onConfirmTextBoxEditing()
                    }
                )
            }
        }
    }
}



@Composable
private fun RowScope.DefaultBottomBarContent(
    canUndo: Boolean,
    canRedo: Boolean,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onAddTextBox: () -> Unit,
    onSaveMeme: () -> Unit
) {
    EditorIconButton(
        icon = Icons.AutoMirrored.Filled.Undo,
        onClick = onUndo,
        enabled = canUndo
    )

    EditorIconButton(
        icon = Icons.AutoMirrored.Filled.Redo,
        onClick = onRedo,
        enabled = canRedo
    )

    Spacer(modifier = Modifier.weight(1f))

    OutlinedButton(
        onClick = onAddTextBox,
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        border = ButtonDefaults.outlinedButtonBorder().copy(
            width = 1.dp,
            brush = Brush.horizontalGradient(
                colors = ExtendedTheme.colorScheme.buttonDefault
            )
        )
    ) {
        Text("Add Text", style = MaterialTheme.typography.labelLarge)
    }

    Spacer(modifier = Modifier.weight(1f))

    Button(
        onClick = onSaveMeme,
        modifier = Modifier.padding(start = 8.dp),
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text("Save Meme", style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center)
    }
}

@Composable
private fun RowScope.TextEditorBottomBarContent(
    selectedOption: TextEditorOption?,
    onOptionSelected: (TextEditorOption?) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Row(
        modifier = Modifier.weight(1f),
        horizontalArrangement = Arrangement.Start
    ) {
        EditorIconButton(
            icon = Icons.Default.Close,
            onClick = onDismiss
        )
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        EditorOptionButton(
            resId = R.drawable.icon_typography,
            isSelected = selectedOption == TextEditorOption.FontFamily,
            onClick = {
                onOptionSelected(
                    if (selectedOption == TextEditorOption.FontFamily) null
                    else TextEditorOption.FontFamily
                )
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        EditorOptionButton(
            resId = R.drawable.icon_text_size,
            isSelected = selectedOption == TextEditorOption.FontSize,
            onClick = {
                onOptionSelected(
                    if (selectedOption == TextEditorOption.FontSize) null
                    else TextEditorOption.FontSize
                )
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        EditorOptionButton(
            resId = R.drawable.icon_text_color,
            isSelected = selectedOption == TextEditorOption.ColorPicker,
            onClick = {
                onOptionSelected(
                    if (selectedOption == TextEditorOption.ColorPicker) null
                    else TextEditorOption.ColorPicker
                )
            }
        )
    }

    // Right section with confirm button
    Row(
        modifier = Modifier.weight(1f),
        horizontalArrangement = Arrangement.End
    ) {
        EditorIconButton(
            icon = Icons.Default.Check,
            onClick = onConfirm
        )
    }
}

@Composable
private fun EditorOptionButton(
    @DrawableRes resId: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        interactionSource = remember {
            MutableInteractionSource(

            )
        },
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) Color(0xFF2A2930)
                else Color.Transparent
            ),
        content = {
            Image(
                modifier = Modifier
                    .padding(2.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick
                    ),
                painter = painterResource(id = resId),
                contentDescription = null,
            )
        },
    )
}

@Composable
private fun FontFamilySelector(
    selectedFont: MemeFont,
    onSelected: (MemeFont) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(MemeFont.entries.toList()) { font ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (selectedFont == font) {
                            Color(0xFF2B2930)
                        } else {
                            Color.Transparent
                        }
                    )
                    .clickable { onSelected(font) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Preview text with font style
                Text(
                    text = "Good",
                    style = font.toTextStyle(
                        color = if (selectedFont == font) Color.White else Color.Unspecified,
                        fontSize = 28f
                    ),
                )
                // Font name
                Text(
                    text = font.displayName,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.sp,
                        color = if (selectedFont == font) Color.White else Color.Unspecified
                    )
                )
            }
        }
    }
}

@Composable
private fun FontSizeSelector(
    selectedFontSize: Float,
    onSizeChanged: (Float) -> Unit
) {
    var fontSize by remember { mutableFloatStateOf(selectedFontSize) }

    AppSlider(
        value = fontSize,
        onValueChange = {
            fontSize = it
            onSizeChanged(it)
        },
        valueRange = 12f..72f,
    )
}

@Composable
private fun ColorSelector(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    val memeColors = MemeTextColor.entries.toTypedArray()

    LazyRow(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(memeColors) { memeColor ->
            val actualColor = memeColor.toFillColor()
            val isSelected = actualColor == selectedColor

            ColorCircle(
                color = actualColor,
                isSelected = isSelected,
                onClick = { onColorSelected(actualColor) }
            )
        }
    }
}

@Composable
private fun ColorCircle(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Log.d("ColorCircle", "Color: $color, isSelected: $isSelected")

    val size = 32.dp
    val selectionRingSize = size * 1.4f
    val selectionRingColor = Color(0x33FFFFFF)

    Canvas(
        modifier = modifier
            .size(selectionRingSize)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = size / 2),
                onClick = onClick
            )
    ) {
        if (isSelected) {
            // Draw selection ring
            drawCircle(
                color = selectionRingColor,
                radius = selectionRingSize.toPx() / 2,
                center = center
            )
        }

        // Draw main color circle
        drawCircle(
            color = color,
            radius = size.toPx() / 2,
            center = center
        )

        // If the color is white, draw a thin gray border
        if (color == Color.White) {
            drawCircle(
                color = Color.Gray,
                radius = size.toPx() / 2,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
        }
    }
}

@Preview
@Composable
fun MemeEditorBottomBarPreview() {
    MasterMemeBackground {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            MemeEditorBottomBar(
                modifier = Modifier.systemBarsPadding(),
                currentLayout = BottomBarLayout.TextEditor,
                selectedColor = Color.Red,
                canRedo = true,
                canUndo = false
            )
            MemeEditorBottomBar(
                modifier = Modifier.systemBarsPadding(),
                currentLayout = BottomBarLayout.Default,
                selectedColor = Color.Red,
                canRedo = false,
                canUndo = true
            )
        }

    }
}



@Preview
@Composable
private fun FontSizeSelectorPreview() {
    FontSizeSelector(selectedFontSize = 16f, {})
}

@Preview(showBackground = true)
@Composable
fun ColorSelectorPreview() {
    MaterialTheme {
        ColorSelector(
            selectedColor = Color.Red,
            onColorSelected = {}
        )
    }
}

@Preview
@Composable
fun ColorCirclePreview() {
    ColorCircle(
        color = Color.Red,
        isSelected = true,
        onClick = { }
    )
}