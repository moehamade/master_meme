package com.mobilecampus.mastermeme.meme.domain.use_case

import com.mobilecampus.mastermeme.meme.domain.model.Meme

interface DeleteMemeUseCase {
    suspend operator fun invoke(meme: Meme)
}