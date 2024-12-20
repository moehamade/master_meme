package com.mobilecampus.mastermeme.meme.data.local.use_case

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.Log
import android.util.TypedValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.IntSize
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.mobilecampus.mastermeme.R
import com.mobilecampus.mastermeme.meme.domain.data_source.MemeDataSource
import com.mobilecampus.mastermeme.meme.domain.model.MemeItem
import com.mobilecampus.mastermeme.meme.domain.model.editor.MemeFont
import com.mobilecampus.mastermeme.meme.domain.model.editor.TextBox
import com.mobilecampus.mastermeme.meme.domain.use_case.SaveMemeUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import kotlin.math.roundToInt

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

        val path = saveMemeInternally(
            context = context,
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

        // <TODO> Vordead remove this logging - we keep it here for debugging purposes
        // Add debug logging
        Log.d("SaveMeme", "Meme saved at: $path")
        val file = File(path)
        Log.d("SaveMeme", """
            Save details:
            - File exists: ${file.exists()}
            - File size: ${file.length()} bytes
            - Save directory: ${file.parentFile?.absolutePath}
            - Directory contents: ${file.parentFile?.list()?.joinToString()}
        """.trimIndent())

        path
    }
}

/**
 * Saves the current meme state as a JPEG image file to the app's internal storage.
 * Handles different image aspect ratios by accounting for letterboxing in the UI display.
 *
 * @param context Android context for accessing internal storage and resources
 * @param backgroundImageResId Resource ID of the background image to be used as the meme template
 * @param textBoxes List of text boxes containing position, content, and styling information to be drawn on the image
 * @param imageSize Current size of the image in the UI (used to calculate proper text positioning)
 * @return String representing the internal file path where the JPEG image was saved
 * @throws IllegalStateException if the background image drawable cannot be loaded
 * @throws Exception if saving the image file fails
 */
private suspend fun saveMemeInternally(
    context: Context,
    backgroundImageResId: Int,
    textBoxes: List<TextBox>,
    imageSize: IntSize
): String = withContext(Dispatchers.IO) {
    val fileName = "meme_${UUID.randomUUID()}.jpg"
    val memeDir = context.getDir("memes", Context.MODE_PRIVATE)
    val internalFile = File(memeDir, fileName)

    val drawable = ContextCompat.getDrawable(context, backgroundImageResId)
        ?: throw IllegalStateException("Could not load background image drawable")

    val backgroundAspectRatio = drawable.intrinsicWidth.toFloat() / drawable.intrinsicHeight.toFloat()
    val (outputWidth, outputHeight, letterboxOffset, visibleHeight) = calculateDimensions(imageSize, backgroundAspectRatio)

    val bitmap = Bitmap.createBitmap(outputWidth, outputHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, bitmap.width, bitmap.height)
    drawable.draw(canvas)

    // Draw all text boxes
    textBoxes.forEach { textBox ->
        drawTextBoxOnCanvas(
            canvas = canvas,
            context = context,
            textBox = textBox,
            visibleHeight = visibleHeight,
            letterboxOffset = letterboxOffset,
            outputHeight = outputHeight
        )
    }

    // Save the bitmap
    try {
        FileOutputStream(internalFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
        }
    } catch (e: Exception) {
        throw Exception("Failed to save meme internally: ${e.message}", e)
    } finally {
        bitmap.recycle()
    }

    internalFile.absolutePath
}

private fun calculateDimensions(
    imageSize: IntSize,
    backgroundAspectRatio: Float
): MemeDimensions {
    val screenAspectRatio = imageSize.width.toFloat() / imageSize.height.toFloat()

    // Determine offset and visible area for letterboxing
    val (visibleHeight, letterboxOffset) = if (backgroundAspectRatio > screenAspectRatio) {
        val height = (imageSize.width / backgroundAspectRatio).roundToInt()
        val offset = (imageSize.height - height) / 2f
        height to offset
    } else {
        imageSize.height to 0f
    }

    // The output bitmap matches the width of the screen space and adjusts height accordingly
    val outputWidth = imageSize.width
    val outputHeight = (outputWidth / backgroundAspectRatio).roundToInt()

    return MemeDimensions(outputWidth, outputHeight, letterboxOffset, visibleHeight)
}

private fun drawTextBoxOnCanvas(
    canvas: Canvas,
    context: Context,
    textBox: TextBox,
    visibleHeight: Int,
    letterboxOffset: Float,
    outputHeight: Int
) {
    // Convert SP to pixels for font size
    val textSizePx = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        textBox.style.fontSize,
        context.resources.displayMetrics
    )

    val typeface = when (textBox.style.font) {
        MemeFont.IMPACT -> ResourcesCompat.getFont(context, R.font.impact) ?: Typeface.DEFAULT_BOLD
        MemeFont.SYSTEM -> Typeface.DEFAULT_BOLD
    }

    // Paint for both outline and fill
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = textSizePx
        this.typeface = typeface
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    // Adjust coordinates for letterboxing
    val x = textBox.position.x
    val y = (textBox.position.y - letterboxOffset).let { adjustedY ->
        if (letterboxOffset > 0) {
            adjustedY * (outputHeight.toFloat() / visibleHeight.toFloat())
        } else adjustedY
    }

    // Account for font baseline
    val adjustedY = y - paint.fontMetrics.top

    // Draw outline
    paint.style = Paint.Style.STROKE
    paint.color = textBox.style.color.toOutlineColor().toArgb()
    paint.strokeWidth = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        4f,
        context.resources.displayMetrics
    )
    canvas.drawText(textBox.text, x, adjustedY, paint)

    // Draw fill
    paint.style = Paint.Style.FILL
    paint.color = textBox.style.color.toFillColor().toArgb()
    canvas.drawText(textBox.text, x, adjustedY, paint)
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
