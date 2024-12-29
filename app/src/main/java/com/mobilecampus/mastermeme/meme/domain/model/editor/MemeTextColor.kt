package com.mobilecampus.mastermeme.meme.domain.model.editor

import androidx.compose.ui.graphics.Color

enum class MemeTextColor {
    WHITE, RED, GREEN, BLUE, YELLOW, ORANGE, PURPLE, PINK, BROWN, CYAN, MAGENTA, GRAY, BLACK;

    // map the enum of color choices to actual colors
    fun toFillColor(): Color {
        return when (this) {
            WHITE -> Color.White
            RED -> Color.Red
            GREEN -> Color.Green
            BLUE -> Color.Blue
            YELLOW -> Color.Yellow
            ORANGE -> Color(0xFFFFA500) // Orange color
            PURPLE -> Color(0xFF800080) // Purple color
            PINK -> Color(0xFFFFC0CB) // Pink color
            BROWN -> Color(0xFFA52A2A) // Brown color
            CYAN -> Color.Cyan
            MAGENTA -> Color.Magenta
            GRAY -> Color.Gray
            BLACK -> Color.Black
        }
    }

    // what outline we use for each color, (for example for white color, we have black outline)
    fun toOutlineColor(): Color {
        return when (this) {
            WHITE -> Color.Black
            RED -> Color.Black
            GREEN -> Color.Black
            BLUE -> Color.White
            YELLOW -> Color.Black
            ORANGE -> Color.Black
            PURPLE -> Color.White
            PINK -> Color.Black
            BROWN -> Color.White
            CYAN -> Color.Black
            MAGENTA -> Color.Black
            GRAY -> Color.Black
            BLACK -> Color.White
        }
    }

    companion object {
        fun fromColor(color: Color): MemeTextColor {
            return when (color) {
                Color.White -> WHITE
                Color.Red -> RED
                Color.Green -> GREEN
                Color.Blue -> BLUE
                Color.Yellow -> YELLOW
                Color(0xFFFFA500) -> ORANGE
                Color(0xFF800080) -> PURPLE
                Color(0xFFFFC0CB) -> PINK
                Color(0xFFA52A2A) -> BROWN
                Color.Cyan -> CYAN
                Color.Magenta -> MAGENTA
                Color.Gray -> GRAY
                Color.Black -> BLACK
                else -> WHITE
            }
        }
    }
}