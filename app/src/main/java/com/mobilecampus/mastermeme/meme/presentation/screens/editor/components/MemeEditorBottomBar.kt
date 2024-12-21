package com.mobilecampus.mastermeme.meme.presentation.screens.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobilecampus.mastermeme.ui.theme.ExtendedTheme
import com.mobilecampus.mastermeme.ui.theme.MasterMemeTheme

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
    modifier: Modifier = Modifier,
    onUndo: () -> Unit = {},
    onRedo: () -> Unit = {},
    onAddTextBox: () -> Unit = {},
    onSaveMeme: () -> Unit = {},
    onDismissTextEditor: () -> Unit = {},
    onConfirmTextEdit: () -> Unit = {},
    onFontFamilySelected: (FontFamily) -> Unit = {},
    onFontSizeChanged: (Float) -> Unit = {},
    onColorSelected: (Color) -> Unit = {}
) {
    var selectedOption by remember { mutableStateOf<TextEditorOption?>(null) }

    Column(modifier = modifier) {
        // Optional content above the BottomAppBar based on selection
        if (currentLayout == BottomBarLayout.TextEditor && selectedOption != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                tonalElevation = 2.dp
            ) {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    when (selectedOption) {
                        TextEditorOption.FontFamily -> FontFamilySelector(onFontFamilySelected)
                        TextEditorOption.FontSize -> FontSizeSelector(onFontSizeChanged)
                        TextEditorOption.ColorPicker -> ColorSelector(onColorSelected)
                        null -> {}
                    }
                }
            }
        }

        BottomAppBar(
            modifier = Modifier
                .fillMaxWidth(),
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
                    onUndo = onUndo,
                    onRedo = onRedo,
                    onAddTextBox = onAddTextBox,
                    onSaveMeme = onSaveMeme
                )

                BottomBarLayout.TextEditor -> TextEditorBottomBarContent(
                    selectedOption = selectedOption,
                    onOptionSelected = { selectedOption = it },
                    onDismiss = onDismissTextEditor,
                    onConfirm = onConfirmTextEdit
                )
            }
        }
    }
}

@Composable
private fun RowScope.DefaultBottomBarContent(
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onAddTextBox: () -> Unit,
    onSaveMeme: () -> Unit
) {
    EditorIconButton(
        icon = Icons.AutoMirrored.Filled.Undo,
        onClick = onUndo,
        enabled = false
    )

    EditorIconButton(
        icon = Icons.AutoMirrored.Filled.Redo,
        onClick = onRedo,
        enabled = false
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
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text("Save Meme", style = MaterialTheme.typography.labelLarge)
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
            icon = Icons.Default.FontDownload,
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
            icon = Icons.Default.FormatSize,
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
            icon = Icons.Default.Palette,
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
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.secondaryContainer
                else Color.Transparent
            )
    ) {
        EditorIconButton(
            icon = icon,
            onClick = onClick
        )
    }
}

@Composable
private fun FontFamilySelector(onSelected: (FontFamily) -> Unit) {
    val fontFamilies = listOf(
        FontFamily.Default to "Default",
        FontFamily.Serif to "Serif",
        FontFamily.SansSerif to "Sans Serif",
        FontFamily.Monospace to "Monospace"
    )

    LazyRow(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(fontFamilies) { (fontFamily, name) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.clickable { onSelected(fontFamily) }
            ) {
                Text(
                    text = "Good",
                    fontFamily = fontFamily,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun FontSizeSelector(onSizeChanged: (Float) -> Unit) {
    var fontSize by remember { mutableFloatStateOf(16f) }

    Slider(
        value = fontSize,
        onValueChange = {
            fontSize = it
            onSizeChanged(it)
        },
        valueRange = 12f..72f,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ColorSelector(onColorSelected: (Color) -> Unit) {
    val colors = listOf(
        Color.White,
        Color.Black,
        Color.Red,
        Color.Green,
        Color.Blue,
        Color.Yellow,
        Color.Cyan,
        Color.Magenta
    )

    LazyRow(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(colors) { color ->
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color)
                    .clickable { onColorSelected(color) }
            )
        }
    }
}

@Preview
@Composable
fun MemeEditorBottomBarPreview() {
    MasterMemeTheme {
        MemeEditorBottomBar(
            modifier = Modifier.systemBarsPadding(),
            currentLayout = BottomBarLayout.Default
        )
    }
}