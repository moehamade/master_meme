package com.mobilecampus.mastermeme.meme.data.local.use_case

import android.content.Context
import androidx.compose.ui.unit.IntSize
import com.mobilecampus.mastermeme.meme.domain.MemeRenderer
import com.mobilecampus.mastermeme.meme.domain.model.editor.TextBox
import com.mobilecampus.mastermeme.meme.domain.use_case.ShareTemporaryMeme
import com.mobilecampus.mastermeme.meme.domain.util.FileManager
import java.io.File

class ShareTemporaryMemeImpl(
    private val context: Context,
    private val fileManager: FileManager,
    private val memeRenderer: MemeRenderer
) : ShareTemporaryMeme {
    override suspend fun invoke(
        backgroundImageResId: Int,
        textBoxes: List<TextBox>,
        imageWidth: Int,
        imageHeight: Int
    ): Result<Unit> = runCatching {
        val tempFile = File(
            context.cacheDir,
            "temp_meme_${System.currentTimeMillis()}.jpg"
        ).apply {
            createNewFile()
        }

        memeRenderer.renderMeme(
            backgroundImageResId = backgroundImageResId,
            textBoxes = textBoxes,
            imageSize = IntSize(imageWidth, imageHeight),
            outputFile = tempFile
        )

        fileManager.shareFiles(setOf(tempFile.absolutePath))
    }
}