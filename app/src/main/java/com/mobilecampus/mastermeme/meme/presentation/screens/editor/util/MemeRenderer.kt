package com.mobilecampus.mastermeme.meme.presentation.screens.editor.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.core.content.ContextCompat
import com.mobilecampus.mastermeme.meme.domain.model.editor.TextBox
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.collections.forEach
import kotlin.math.roundToInt

class MemeRenderer(private val context: Context) {
    suspend fun renderMeme(
        backgroundImageResId: Int,
        textBoxes: List<TextBox>,
        imageSize: IntSize,
        outputFile : File
    ): String = withContext(Dispatchers.IO) {
        val drawable = ContextCompat.getDrawable(context, backgroundImageResId)
            ?: throw IllegalStateException("Could not load background image drawable")

        val backgroundAspectRatio =
            drawable.intrinsicWidth.toFloat() / drawable.intrinsicHeight.toFloat()
        val dimensions = calculateDimensions(imageSize, backgroundAspectRatio)

        val bitmap = createMemeBitmap(drawable, dimensions, textBoxes)
        saveBitmap(bitmap, outputFile)

        outputFile.absolutePath
    }

    private fun calculateDimensions(
        imageSize: IntSize,
        backgroundAspectRatio: Float
    ): MemeDimensions {
        val screenAspectRatio = imageSize.width.toFloat() / imageSize.height.toFloat()

        val (visibleHeight, letterboxOffset) = if (backgroundAspectRatio > screenAspectRatio) {
            val height = (imageSize.width / backgroundAspectRatio).roundToInt()
            val offset = (imageSize.height - height) / 2f
            height to offset
        } else {
            imageSize.height to 0f
        }

        val outputWidth = imageSize.width
        val outputHeight = (outputWidth / backgroundAspectRatio).roundToInt()

        return MemeDimensions(outputWidth, outputHeight, letterboxOffset, visibleHeight)
    }

    private fun createMemeBitmap(
        drawable: Drawable,
        dimensions: MemeDimensions,
        textBoxes: List<TextBox>
    ): Bitmap {
        return Bitmap.createBitmap(
            dimensions.outputWidth,
            dimensions.outputHeight,
            Bitmap.Config.ARGB_8888
        ).apply {
            val canvas = Canvas(this)
            drawable.setBounds(0, 0, width, height)
            drawable.draw(canvas)

            textBoxes.forEach { textBox ->
                drawTextBox(canvas, textBox, dimensions)
            }
        }
    }

    private fun drawTextBox(
        canvas: Canvas,
        textBox: TextBox,
        dimensions: MemeDimensions
    ) {
        val textPainter = MemeTextPainter(context, textBox.style)
        val position = calculateTextPosition(
            textBox.position,
            dimensions.letterboxOffset,
            dimensions.visibleHeight,
            dimensions.outputHeight
        )
        textPainter.drawText(canvas, textBox.text, position)
    }

    private fun calculateTextPosition(
        originalPosition: Offset,
        letterboxOffset: Float,
        visibleHeight: Int,
        outputHeight: Int
    ): Offset {
        val adjustedY = (originalPosition.y - letterboxOffset).let { y ->
            if (letterboxOffset > 0) {
                y * (outputHeight.toFloat() / visibleHeight.toFloat())
            } else y
        }
        return Offset(originalPosition.x, adjustedY)
    }

    private suspend fun saveBitmap(bitmap: Bitmap, outputFile: File) {
        withContext(Dispatchers.IO) {
            try {
                FileOutputStream(outputFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
                }
            } catch (e: Exception) {
                throw Exception("Failed to save meme internally: ${e.message}", e)
            } finally {
                bitmap.recycle()
            }
        }
    }
}

private data class MemeDimensions(
    val outputWidth: Int,
    val outputHeight: Int,
    val letterboxOffset: Float,
    val visibleHeight: Int
)