package com.mobilecampus.mastermeme.meme.domain.use_case

interface GetTemplatesUseCase {
    suspend operator fun invoke(): List<String>
}