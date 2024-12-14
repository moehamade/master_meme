package com.mobilecampus.mastermeme.meme.domain.use_case

import com.mobilecampus.mastermeme.meme.domain.model.MemeItem

interface GetTemplatesUseCase {
    suspend operator fun invoke(): List<MemeItem.Template>
}