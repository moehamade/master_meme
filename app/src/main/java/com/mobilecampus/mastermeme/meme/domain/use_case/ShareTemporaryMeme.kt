package com.mobilecampus.mastermeme.meme.domain.use_case

import com.mobilecampus.mastermeme.meme.domain.model.editor.TextBox

interface ShareTemporaryMeme {
    suspend operator fun invoke(
        backgroundImageResId: Int,
        textBoxes: List<TextBox>,
        imageWidth: Int,
        imageHeight: Int
    ): Result<Unit>
}