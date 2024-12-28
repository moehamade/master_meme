package com.mobilecampus.mastermeme.meme.domain

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.TypedValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.res.ResourcesCompat
import com.mobilecampus.mastermeme.R
import com.mobilecampus.mastermeme.meme.domain.model.editor.MemeFont
import com.mobilecampus.mastermeme.meme.domain.model.editor.MemeTextStyle

internal class MemeTextPainter(
    private val context: Context,
    private val style: MemeTextStyle
) {
    private val textSizePx = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        style.fontSize,
        context.resources.displayMetrics
    )

    private val basePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = textSizePx
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    fun drawText(canvas: Canvas, text: String, position: Offset) {
        when (style.font) {
            MemeFont.STROKE -> {
                // Only stroke, no fill
                val paint = basePaint.apply {
                    typeface = Typeface.DEFAULT_BOLD
                    style = Paint.Style.STROKE
                    strokeWidth = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        3f,
                        context.resources.displayMetrics
                    )
                }
                paint.color = style.color.toFillColor().toArgb()
                drawTextWithBaseline(canvas, text, position, paint)
            }
            MemeFont.SHADOWED -> {
                val paint = basePaint.apply {
                    typeface = Typeface.DEFAULT_BOLD
                    style = Paint.Style.FILL
                }
                paint.setShadowLayer(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        3f,
                        context.resources.displayMetrics
                    ),
                    2f, 2f,
                    Color.Black.toArgb()
                )
                paint.color = style.color.toFillColor().toArgb()
                drawTextWithBaseline(canvas, text, position, paint)
            }
            MemeFont.OUTLINE -> {
                val paint = basePaint.apply {
                    typeface = Typeface.DEFAULT_BOLD
                    strokeWidth = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        4f,
                        context.resources.displayMetrics
                    )
                }
                // Draw outline
                paint.style = Paint.Style.STROKE
                paint.color = style.color.toOutlineColor().toArgb()
                drawTextWithBaseline(canvas, text, position, paint)

                // Draw fill
                paint.style = Paint.Style.FILL
                paint.color = style.color.toFillColor().toArgb()
                drawTextWithBaseline(canvas, text, position, paint)
            }
            MemeFont.RETRO -> {
                val paint = basePaint.apply {
                    typeface = Typeface.DEFAULT_BOLD
                    letterSpacing = 0.2f
                }
                drawOutlinedText(canvas, text, position, paint)
            }
            MemeFont.HANDWRITTEN -> {
                val paint = basePaint.apply {
                    typeface = Typeface.SERIF
                    letterSpacing = 0.1f
                }
                drawOutlinedText(canvas, text, position, paint)
            }
            MemeFont.COMIC -> {
                val paint = basePaint.apply {
                    typeface = Typeface.SANS_SERIF
                }
                drawOutlinedText(canvas, text, position, paint)
            }
            MemeFont.ROBOTO_BOLD -> {
                val paint = basePaint.apply {
                    typeface = Typeface.DEFAULT_BOLD
                }
                drawOutlinedText(canvas, text, position, paint)
            }
            MemeFont.IMPACT -> {
                val paint = basePaint.apply {
                    typeface = ResourcesCompat.getFont(context, R.font.impact) ?: Typeface.DEFAULT_BOLD
                }
                drawOutlinedText(canvas, text, position, paint)
            }
            MemeFont.SYSTEM -> {
                val paint = basePaint.apply {
                    typeface = Typeface.DEFAULT
                }
                drawOutlinedText(canvas, text, position, paint)
            }
        }
    }

    private fun drawOutlinedText(canvas: Canvas, text: String, position: Offset, paint: Paint) {
        // Draw outline
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            4f,
            context.resources.displayMetrics
        )
        val outlineColor = style.color.toOutlineColor()
        paint.color = outlineColor.toArgb()
        drawTextWithBaseline(canvas, text, position, paint)

        // Draw fill
        paint.style = Paint.Style.FILL
        val fillColor = style.color.toFillColor()
        paint.color = fillColor.toArgb()
        drawTextWithBaseline(canvas, text, position, paint)
    }

    private fun drawTextWithBaseline(canvas: Canvas, text: String, position: Offset, paint: Paint) {
        val adjustedY = position.y - paint.fontMetrics.top
        canvas.drawText(text, position.x, adjustedY, paint)
    }
}