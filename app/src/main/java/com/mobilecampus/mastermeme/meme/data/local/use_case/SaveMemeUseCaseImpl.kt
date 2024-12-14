package com.mobilecampus.mastermeme.meme.data.local.use_case

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.Log
import android.util.TypedValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
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
        imageOffsetX: Float,
        imageOffsetY: Float,
        imageWidth: Int,
        imageHeight: Int
    ): Result<String> = runCatching {

        val path = saveMemeInternally(
            context = context,
            backgroundImageResId = backgroundImageResId,
            textBoxes = textBoxes,
            imageOffset = Offset(imageOffsetX, imageOffsetY),
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
 *
 * @param context Android context for accessing internal storage
 * @param backgroundImageResId Resource ID of the background image
 * @param textBoxes List of text boxes to draw on the image
 * @param imageOffset Current offset of the image in the UI (not used in this code, consider removing if unnecessary)
 * @param imageSize Current size of the image in the UI
 * @return String representing the internal file path where the image was saved
 * @throws Exception if saving fails
 */
private suspend fun saveMemeInternally(
    context: Context,
    backgroundImageResId: Int,
    textBoxes: List<TextBox>,
    imageOffset: Offset,
    imageSize: IntSize
): String = withContext(Dispatchers.IO) {
    // Create a unique filename
    val fileName = "meme_${UUID.randomUUID()}.jpg"

    // Get or create internal directory for meme images
    val memeDir = context.getDir("memes", Context.MODE_PRIVATE)
    val internalFile = File(memeDir, fileName)

    // Attempt to load and decode the background image
    val drawable = ContextCompat.getDrawable(context, backgroundImageResId)
        ?: throw IllegalStateException("Could not load background image drawable")

    // Convert drawable to bitmap
    val bitmap = drawable.toBitmap() ?: throw IllegalStateException("Drawable to bitmap conversion failed")

    // Create a canvas to draw text onto the bitmap
    val canvas = Canvas(bitmap)

    // Precompute scale factors to map UI coordinates to bitmap coordinates
    val scaleX = bitmap.width.toFloat() / imageSize.width
    val scaleY = bitmap.height.toFloat() / imageSize.height

    // Precompute stroke width dimension conversion (outline thickness)
    val outlineThickness = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        4f,
        context.resources.displayMetrics
    )

    // Process each text box
    textBoxes.forEach { textBox ->
        val textSizePx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            textBox.style.fontSize,
            context.resources.displayMetrics
        )

        // Select typeface
        val typeface = when (textBox.style.font) {
            MemeFont.IMPACT -> ResourcesCompat.getFont(context, R.font.impact)
            MemeFont.SYSTEM -> Typeface.DEFAULT_BOLD
        }

        // Set up paint for both outline and fill
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.textSize = textSizePx
            this.typeface = typeface
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }

        // Calculate final coordinates in the bitmap
        val x = textBox.position.x * scaleX
        val y = textBox.position.y * scaleY

        // Adjust baseline according to font metrics
        val baseline = y - paint.fontMetrics.top

        // Draw text outline
        paint.style = Paint.Style.STROKE
        paint.color = Color.BLACK
        paint.strokeWidth = outlineThickness
        canvas.drawText(textBox.text, x, baseline, paint)

        // Draw text fill
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        canvas.drawText(textBox.text, x, baseline, paint)
    }

    // Save bitmap as JPEG
    try {
        FileOutputStream(internalFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
        }
    } catch (e: Exception) {
        bitmap.recycle()
        throw Exception("Failed to save meme internally: ${e.message}", e)
    }

    // Free bitmap memory
    bitmap.recycle()

    // Return the internal file path
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
