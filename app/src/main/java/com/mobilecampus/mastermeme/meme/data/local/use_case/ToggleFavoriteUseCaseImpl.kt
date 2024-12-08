package com.mobilecampus.mastermeme.meme.data.local.use_case

import com.mobilecampus.mastermeme.meme.domain.data_source.MemeDataSource
import com.mobilecampus.mastermeme.meme.domain.use_case.ToggleFavoriteUseCase

class ToggleFavoriteUseCaseImpl(
    private val dataSource: MemeDataSource
) : ToggleFavoriteUseCase {
    override suspend operator fun invoke(memeId: Int) {
        dataSource.toggleFavorite(memeId)
    }
}