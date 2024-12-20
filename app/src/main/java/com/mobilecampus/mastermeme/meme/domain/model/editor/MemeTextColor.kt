package com.mobilecampus.mastermeme.meme.domain.model.editor

import androidx.compose.ui.graphics.Color

enum class MemeTextColor {
    WHITE, RED, GREEN, BLUE;

    // map the enum of color choices to actual colors
    fun toFillColor(): Color {
        return when (this) {
            WHITE -> Color.White
            RED -> Color.Red
            GREEN -> Color.Green
            BLUE -> Color.Blue
        }
    }

    // what outline we use for each color, (for example for white color, we have black outline)
    fun toOutlineColor(): Color {
        return when (this) {
            WHITE -> Color.Black
            RED -> Color.Black
            GREEN -> Color.Black
            BLUE -> Color.White
        }
    }
}