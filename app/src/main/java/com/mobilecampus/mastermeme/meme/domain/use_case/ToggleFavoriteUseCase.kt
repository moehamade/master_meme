package com.mobilecampus.mastermeme.meme.domain.use_case

interface ToggleFavoriteUseCase {
    suspend operator fun invoke(memeId: Int)
}