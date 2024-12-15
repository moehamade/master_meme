package com.mobilecampus.mastermeme.meme.domain.use_case

import com.mobilecampus.mastermeme.meme.domain.model.MemeItem

// 4. UseCase
interface DeleteMemeUseCase {
    suspend operator fun invoke(ids: Set<Int>)
}