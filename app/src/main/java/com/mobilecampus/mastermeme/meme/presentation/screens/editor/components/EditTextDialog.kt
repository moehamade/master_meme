package com.mobilecampus.mastermeme.meme.presentation.screens.editor.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mobilecampus.mastermeme.R
import com.mobilecampus.mastermeme.ui.theme.MasterMemeTheme

@Composable
fun EditTextDialog(
    initialText: String,
    onDismiss: () -> Unit = {},
    onConfirm: (String) -> Unit = { _ -> },
) {
    var text by remember { mutableStateOf(initialText) }
    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
    val textFieldValue = remember { mutableStateOf(TextFieldValue(initialText)) }

    val darkColorScheme = darkColorScheme()

    Dialog(onDismissRequest = onDismiss) {
        MaterialTheme(
            colorScheme = darkColorScheme
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextField(
                        value = textFieldValue.value,
                        onValueChange = { newValue ->
                            textFieldValue.value = newValue
                            text = newValue.text
                        },
                        label = { Text(stringResource(R.string.dialog_label_text)) },
                        modifier = Modifier.focusRequester(focusRequester)
                    )

                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                        textFieldValue.value = textFieldValue.value.copy(
                            selection = TextRange(0, initialText.length)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(stringResource(R.string.dialog_cancel_button))
                        }
                        TextButton(onClick = { onConfirm(text) }) {
                            Text(stringResource(R.string.dialog_ok_button))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun DraggableTextBoxPreview() {
    MasterMemeTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            EditTextDialog(
                initialText = "Hello, World!",
            )
        }
    }
}
