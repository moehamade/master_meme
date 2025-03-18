package com.mobilecampus.mastermeme.meme.domain.use_case

interface DeleteMemeUseCase {
    suspend operator fun invoke(ids: Set<Int>)
}