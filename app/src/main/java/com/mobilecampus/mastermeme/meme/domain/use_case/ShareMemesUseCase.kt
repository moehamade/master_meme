package com.mobilecampus.mastermeme.meme.domain.use_case

interface ShareMemesUseCase {
    suspend operator fun invoke(ids: Set<Int>)
}