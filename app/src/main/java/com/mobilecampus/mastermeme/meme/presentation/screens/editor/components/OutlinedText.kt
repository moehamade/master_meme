package com.mobilecampus.mastermeme.meme.presentation.screens.editor.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.mobilecampus.mastermeme.R
import com.mobilecampus.mastermeme.meme.domain.model.editor.MemeFont
import com.mobilecampus.mastermeme.meme.domain.model.editor.MemeTextStyle

@Composable
fun OutlinedText(
    text: String,
    style: MemeTextStyle,
    outlineWidth: Float,
    paddingHorizontal: Dp,
    paddingVertical: Dp,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val context = LocalContext.current

    val typeface = when (style.font) {
        MemeFont.IMPACT -> ResourcesCompat.getFont(context, R.font.impact)
        MemeFont.SYSTEM -> Typeface.DEFAULT_BOLD
        MemeFont.ROBOTO_BOLD -> Typeface.DEFAULT_BOLD
        MemeFont.COMIC -> Typeface.SANS_SERIF
        MemeFont.STROKE,
        MemeFont.OUTLINE -> Typeface.DEFAULT_BOLD
        MemeFont.SHADOWED -> Typeface.DEFAULT_BOLD
        MemeFont.RETRO -> Typeface.DEFAULT_BOLD
        MemeFont.HANDWRITTEN -> Typeface.SERIF
    }

    val textMeasurePaint = Paint().apply {
        isAntiAlias = true
        textSize = with(density) { style.fontSize.sp.toPx() }
        this.typeface = typeface
        this.style = Paint.Style.FILL
    }

    val textWidth = textMeasurePaint.measureText(text)
    val fontMetrics = textMeasurePaint.fontMetrics
    val textHeight = fontMetrics.descent - fontMetrics.ascent

    val totalPaddingHorizontalPx = with(density) { paddingHorizontal.toPx() + outlineWidth }
    val totalPaddingVerticalPx = with(density) { paddingVertical.toPx() + outlineWidth }
    val canvasWidth = textWidth + totalPaddingHorizontalPx * 2
    val canvasHeight = textHeight + totalPaddingVerticalPx * 2

    Canvas(
        modifier = modifier.size(
            width = with(density) { canvasWidth.toDp() },
            height = with(density) { canvasHeight.toDp() }
        )
    ) {
        val paint = Paint().apply {
            isAntiAlias = true
            textSize = with(density) { style.fontSize.sp.toPx() }
            this.typeface = typeface
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND

            // Copy letter spacing from measure paint
            letterSpacing = textMeasurePaint.letterSpacing
        }

        val x = totalPaddingHorizontalPx
        val y = -paint.ascent() + totalPaddingVerticalPx

        when (style.font) {
            MemeFont.STROKE -> {
                // Only stroke, no fill
                paint.style = Paint.Style.STROKE
                paint.color = style.color.toFillColor().toArgb()
                paint.strokeWidth = outlineWidth * 1.5f
                drawContext.canvas.nativeCanvas.drawText(text, x, y, paint)
            }
            MemeFont.SHADOWED -> {
                // Draw shadow first
                paint.style = Paint.Style.FILL
                paint.setShadowLayer(
                    outlineWidth * 1.5f,
                    outlineWidth * 0.5f,
                    outlineWidth * 0.5f,
                    Color.Black.toArgb()
                )
                paint.color = style.color.toFillColor().toArgb()
                drawContext.canvas.nativeCanvas.drawText(text, x, y, paint)

                // Clear shadow for main text
                paint.clearShadowLayer()
            }
            else -> {
                // Standard outline and fill for other fonts
                // Outline
                paint.style = Paint.Style.STROKE
                paint.color = style.color.toOutlineColor().toArgb()
                paint.strokeWidth = outlineWidth
                drawContext.canvas.nativeCanvas.drawText(text, x, y, paint)

                // Fill
                paint.style = Paint.Style.FILL
                paint.color = style.color.toFillColor().toArgb()
                drawContext.canvas.nativeCanvas.drawText(text, x, y, paint)
            }
        }
    }
}

