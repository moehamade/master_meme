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
import com.mobilecampus.mastermeme.meme.domain.MemeRenderer


class SaveMemeUseCaseImpl(
    private val context: Context,
    private val dataSource: MemeDataSource,
    private val memeRenderer: MemeRenderer // Inject the renderer
) : SaveMemeUseCase {
    override suspend fun saveMeme(
        backgroundImageResId: Int,
        textBoxes: List<TextBox>,
        imageWidth: Int,
        imageHeight: Int
    ): Result<String> = runCatching {
        val fileName = "meme_${UUID.randomUUID()}.jpg"
        val memeDir = context.getDir("memes", Context.MODE_PRIVATE)
        val outputFile = File(memeDir, fileName)

        memeRenderer.renderMeme(
            backgroundImageResId = backgroundImageResId,
            textBoxes = textBoxes,
            imageSize = IntSize(imageWidth, imageHeight),
            outputFile = outputFile
        )

        dataSource.saveMeme(
            MemeItem.ImageMeme(
                imageUri = outputFile.absolutePath,
                createdAt = System.currentTimeMillis(),
                description = "",
                isFavorite = false
            )
        )

        outputFile.absolutePath
    }
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
