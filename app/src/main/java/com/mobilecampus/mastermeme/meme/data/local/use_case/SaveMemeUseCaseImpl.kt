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
    // Generate unique filename for the meme
    val fileName = "meme_${UUID.randomUUID()}.jpg"
    val memeDir = context.getDir("memes", Context.MODE_PRIVATE)
    val internalFile = File(memeDir, fileName)

    // Load the background image drawable
    val drawable = ContextCompat.getDrawable(context, backgroundImageResId)
        ?: throw IllegalStateException("Could not load background image drawable")

    // Calculate aspect ratios to handle letterboxing
    // Letterboxing occurs when the image's aspect ratio doesn't match the screen's aspect ratio,
    // resulting in black bars either at the top/bottom or sides
    val backgroundAspectRatio = drawable.intrinsicWidth.toFloat() / drawable.intrinsicHeight.toFloat()
    val screenAspectRatio = imageSize.width.toFloat() / imageSize.height.toFloat()

    // Determine the actual visible height of the image in the UI and calculate letterbox offset
    // This is crucial for correct text positioning when the image has black bars
    val (visibleHeight, letterboxOffset) = if (backgroundAspectRatio > screenAspectRatio) {
        // Image is wider than screen ratio - will have letterboxing on top/bottom
        val height = (imageSize.width / backgroundAspectRatio).toInt()
        val offset = (imageSize.height - height) / 2f  // Distance from top of screen to actual image
        height to offset
    } else {
        // Image fills height completely - no letterboxing needed
        imageSize.height to 0f
    }

    // Create the output bitmap with dimensions that preserve the original aspect ratio
    // We use the screen width as reference and calculate height based on the aspect ratio
    val outputWidth = imageSize.width
    val outputHeight = (outputWidth / backgroundAspectRatio).toInt()

    // Create a new bitmap with the calculated dimensions
    val bitmap = Bitmap.createBitmap(
        outputWidth,
        outputHeight,
        Bitmap.Config.ARGB_8888
    )

    // Draw the background image onto the bitmap
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, bitmap.width, bitmap.height)
    drawable.draw(canvas)

    // Process each text box to be drawn on the meme
    textBoxes.forEach { textBox ->
        // Convert SP units to pixels for text size
        val textSizePx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            textBox.style.fontSize,
            context.resources.displayMetrics
        )

        // Select the appropriate font
        val typeface = when (textBox.style.font) {
            MemeFont.IMPACT -> ResourcesCompat.getFont(context, R.font.impact)
            MemeFont.SYSTEM -> Typeface.DEFAULT_BOLD
        }

        // Configure the paint object for text rendering
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.textSize = textSizePx
            this.typeface = typeface
            strokeJoin = Paint.Join.ROUND  // Smooth corners for outline
            strokeCap = Paint.Cap.ROUND    // Smooth ends for outline
        }

        // Calculate the correct text position:
        // 1. Start with the raw x coordinate (no adjustment needed for width)
        // 2. Subtract letterbox offset from y to account for black bars
        val x = textBox.position.x
        val y = textBox.position.y - letterboxOffset

        // If we have letterboxing, scale the y coordinate to match the output bitmap's dimensions
        val scaledY = if (letterboxOffset > 0) {
            y * (outputHeight.toFloat() / visibleHeight.toFloat())
        } else {
            y
        }

        // Adjust Y position to account for text baseline
        // Android draws text from the baseline, so we need to offset by the font metrics
        val adjustedY = scaledY - paint.fontMetrics.top

        paint.style = Paint.Style.STROKE
        paint.color = textBox.style.color.toOutlineColor().toArgb()
        paint.strokeWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            4f,  // 4dp stroke width for outline
            context.resources.displayMetrics
        )
        canvas.drawText(textBox.text, x, adjustedY, paint)

        paint.style = Paint.Style.FILL
        paint.color = textBox.style.color.toFillColor().toArgb()
        canvas.drawText(textBox.text, x, adjustedY, paint)
    }

    // Save the final bitmap as a JPEG file
    try {
        FileOutputStream(internalFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)  // 85% quality
        }
    } catch (e: Exception) {
        bitmap.recycle()
        throw Exception("Failed to save meme internally: ${e.message}", e)
    }

    // Clean up bitmap to free memory
    bitmap.recycle()

    // Return the path where the meme was saved
    internalFile.absolutePath
}

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
