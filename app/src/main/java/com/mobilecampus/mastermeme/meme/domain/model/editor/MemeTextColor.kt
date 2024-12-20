package com.mobilecampus.mastermeme.meme.domain.model.editor

import androidx.compose.ui.graphics.Color

enum class MemeTextColor {
    WHITE, RED, GREEN, BLUE
}

// map the enum of color choices to actual colors
fun MemeTextColor.toFillColor(): Color {
    return when (this) {
        MemeTextColor.WHITE -> Color.White
        MemeTextColor.RED -> Color.Red
        MemeTextColor.GREEN -> Color.Green
        MemeTextColor.BLUE -> Color.Blue
    }
}

// what outline we use for each color, (for example for white color, we have black outline)
fun MemeTextColor.toOutlineColor(): Color {
    return when (this) {
        MemeTextColor.WHITE -> Color.Black
        MemeTextColor.RED -> Color.Black
        MemeTextColor.GREEN -> Color.Black
        MemeTextColor.BLUE -> Color.White
    }
}