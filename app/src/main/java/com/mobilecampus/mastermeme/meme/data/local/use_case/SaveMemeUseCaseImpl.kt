package com.mobilecampus.mastermeme.meme.data.local.use_case

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.core.content.ContextCompat
import com.mobilecampus.mastermeme.R
import com.mobilecampus.mastermeme.meme.domain.data_source.MemeDataSource
import com.mobilecampus.mastermeme.meme.domain.model.MemeItem
import com.mobilecampus.mastermeme.meme.domain.model.editor.MemeFont
import com.mobilecampus.mastermeme.meme.domain.model.editor.MemeTextStyle
import com.mobilecampus.mastermeme.meme.domain.model.editor.TextBox
import com.mobilecampus.mastermeme.meme.domain.use_case.SaveMemeUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import kotlin.math.roundToInt
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.res.ResourcesCompat


class SaveMemeUseCaseImpl(
    private val context: Context,
    private val dataSource: MemeDataSource,
) : SaveMemeUseCase {
    override suspend fun saveMeme(
        backgroundImageResId: Int,
        textBoxes: List<TextBox>,
        imageWidth: Int,
        imageHeight: Int
    ): Result<String> = runCatching {
        val path = MemeRenderer(context).renderMeme(
            backgroundImageResId = backgroundImageResId,
            textBoxes = textBoxes,
            imageSize = IntSize(imageWidth, imageHeight)
        )

        dataSource.saveMeme(
            MemeItem.ImageMeme(
                imageUri = path,
                createdAt = System.currentTimeMillis(),
                description = "",
                isFavorite = false
            )
        )

        path
    }
}

private class MemeRenderer(private val context: Context) {
    suspend fun renderMeme(
        backgroundImageResId: Int,
        textBoxes: List<TextBox>,
        imageSize: IntSize
    ): String = withContext(Dispatchers.IO) {
        val fileName = "meme_${UUID.randomUUID()}.jpg"
        val memeDir = context.getDir("memes", Context.MODE_PRIVATE)
        val outputFile = File(memeDir, fileName)

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

    private fun saveBitmap(bitmap: Bitmap, outputFile: File) {
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

private class MemeTextPainter(
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

private data class MemeDimensions(
    val outputWidth: Int,
    val outputHeight: Int,
    val letterboxOffset: Float,
    val visibleHeight: Int
)

/**
 * <TODO> Consider for your image loading usecase
 */
fun loadMemeFromInternal(context: Context, internalPath: String): Bitmap? {
    return runCatching {
        BitmapFactory.decodeFile(internalPath)
    }.getOrNull()
}

/**
 * <TODO> Consider for your deletion usecase
 */
fun deleteMemeFromInternal(internalPath: String): Boolean {
    return runCatching {
        File(internalPath).delete()
    }.getOrDefault(false)
}
