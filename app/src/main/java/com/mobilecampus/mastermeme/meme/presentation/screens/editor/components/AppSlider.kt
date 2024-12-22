package com.mobilecampus.mastermeme.meme.presentation.screens.editor.components


import android.R.attr.track
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceDim.copy(alpha = 0.3f),
                                shape = CircleShape
                            )
                    )
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceDim,
                                shape = CircleShape
                            )
                    )
                }
            },
            track = { state ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                ) {
                    // Inactive (background) track
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = MaterialTheme.colorScheme.surfaceDim,
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                    // Active track
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(state.value)
                            .fillMaxHeight()
                            .background(
                                color = MaterialTheme.colorScheme.surfaceDim,
                                shape = RoundedCornerShape(2.dp)
                            )
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