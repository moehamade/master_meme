package com.mobilecampus.mastermeme.meme.presentation.screens.editor.components


import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobilecampus.mastermeme.ui.theme.MasterMemeTheme
import com.mobilecampus.mastermeme.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    sliderColor : Color = MaterialTheme.colorScheme.surfaceDim
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Aa",
            style = MaterialTheme.typography.bodySmall,
            color = White
        )

        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            enabled = enabled,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                activeTrackColor = MaterialTheme.colorScheme.surfaceDim,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceDim,
                thumbColor = MaterialTheme.colorScheme.surfaceDim
            ),
            thumb = {
                Canvas(
                    modifier = Modifier.size(32.dp),
                    onDraw = {
                        // Draw outer circle
                        drawCircle(
                            color = sliderColor.copy(alpha = 0.3f),
                            radius = size.width / 2
                        )
                        // Draw inner circle
                        drawCircle(
                            color = sliderColor,
                            radius = size.width / 4
                        )
                    }
                )
            },
            track = { state ->
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                ) {
                    val trackStrokeWidth = 1.dp.toPx()
                    val trackCornerRadius = 1.dp.toPx()

                    drawRoundRect(
                        color = sliderColor,
                        size = size,
                        cornerRadius = CornerRadius(trackCornerRadius),
                        style = Stroke(width = trackStrokeWidth)
                    )
                }
            }
        )

        Text(
            text = "Aa",
            style = MaterialTheme.typography.headlineLarge,
            color = White
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun AppSliderPreview() {
    MasterMemeTheme {
        var slider1 by remember { mutableFloatStateOf(0.2f) }
        AppSlider(
            value = slider1,
            onValueChange = { newValue -> slider1 = newValue },
            valueRange = 0f..1f
        )
    }
}