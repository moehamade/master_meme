package com.mobilecampus.mastermeme.core.presentation.design_system

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobilecampus.mastermeme.ui.theme.ExtendedTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradientFilledButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = RoundedCornerShape(50),
    backgroundGradient: List<Color> = ExtendedTheme.colorScheme.buttonDefault,
    backgroundGradientPressed: List<Color> = ExtendedTheme.colorScheme.buttonPressed,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(shape)
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = ripple(
                    color = backgroundGradientPressed[1]
                )
            )
    ) {
        Row(
            modifier = modifier
                .clip(shape)
                .background(
                    brush = Brush.linearGradient(
                        colors = backgroundGradient,
                        start = Offset(0.0f, 50.0f),
                        end = Offset(20f, 100.0f)

                    )
                )
                .padding(contentPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

@Preview("default", "round")
@Preview("dark theme", "round", uiMode = UI_MODE_NIGHT_YES)
@Preview("large font", "round", fontScale = 2f)
@Composable
private fun GradientButtonPreview() {
    MaterialTheme {
        GradientFilledButton(onClick = {}) {
            Text(text = "Demo")
        }
    }
}

@Preview("default", "rectangle")
@Preview("dark theme", "rectangle", uiMode = UI_MODE_NIGHT_YES)
@Preview("large font", "rectangle", fontScale = 2f)
@Composable
private fun RectangleGradientButtonPreview() {
    MaterialTheme {
        GradientFilledButton(
            onClick = {}, shape = RoundedCornerShape(0.dp)
        ) {
            Text(text = "Demo")
        }
    }
}