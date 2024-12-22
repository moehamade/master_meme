package com.mobilecampus.mastermeme.meme.domain.model.editor

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

enum class MemeFont(
    val displayName: String,
    val fontFamily: FontFamily = FontFamily.Default,
    val textStyle: TextStyle = TextStyle()
) {
    IMPACT(
        displayName = "Impact",
        textStyle = TextStyle(
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.sp
        )
    ),
    SYSTEM(
        displayName = "System",
        fontFamily = FontFamily.Default,
        textStyle = TextStyle(
            fontWeight = FontWeight.Normal
        )
    ),
    ROBOTO_BOLD(
        displayName = "Roboto Bold",
        fontFamily = FontFamily.Default,
        textStyle = TextStyle(
            fontWeight = FontWeight.Bold
        )
    ),
    COMIC(
        displayName = "Comic",
        fontFamily = FontFamily.SansSerif,
        textStyle = TextStyle(
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.5.sp
        )
    ),
    STROKE(
        displayName = "Stroke",
        textStyle = TextStyle(
            drawStyle = Stroke(
                width = 2f,
                join = StrokeJoin.Round
            )
        )
    ),
    SHADOWED(
        displayName = "Shadowed",
        textStyle = TextStyle(
            shadow = Shadow(
                offset = Offset(2f, 2f),
                blurRadius = 3f
            )
        )
    ),
    OUTLINE(
        displayName = "Outline",
        textStyle = TextStyle(
            drawStyle = Stroke(
                width = 3f,
                join = StrokeJoin.Round
            )
        )
    ),
    RETRO(
        displayName = "Retro",
        textStyle = TextStyle(
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 2.sp
        )
    ),
    HANDWRITTEN(
        displayName = "Handwritten",
        fontFamily = FontFamily.Cursive,
        textStyle = TextStyle(
            fontWeight = FontWeight.Normal,
            letterSpacing = 1.sp
        )
    );

    fun toTextStyle(color: Color, fontSize: Float): TextStyle {
        return textStyle.merge(
            TextStyle(
                fontSize = fontSize.sp,
                color = color,
                fontFamily = fontFamily
            )
        )
    }
}

// Move color mapping to a separate object
object MemeColorMapper {
    fun colorToMemeColor(color: Color): MemeTextColor {
        return MemeTextColor.entries.find { it.toFillColor() == color } ?: MemeTextColor.WHITE
    }

    fun memeColorToColor(memeColor: MemeTextColor): Color {
        return memeColor.toFillColor()
    }
}