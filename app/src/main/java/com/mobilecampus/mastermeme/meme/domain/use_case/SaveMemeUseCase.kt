package com.mobilecampus.mastermeme.meme.domain.use_case

import com.mobilecampus.mastermeme.meme.domain.model.editor.TextBox

interface SaveMemeUseCase {
    suspend fun saveMeme(
        backgroundImageResId: Int,
        textBoxes: List<TextBox>,
        imageOffsetX: Float,
        imageOffsetY: Float,
        imageWidth: Int,
        imageHeight: Int
    ): Result<String>
}