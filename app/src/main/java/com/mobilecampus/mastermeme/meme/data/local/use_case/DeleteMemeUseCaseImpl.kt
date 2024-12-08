package com.mobilecampus.mastermeme.meme.data.local.use_case

import com.mobilecampus.mastermeme.meme.domain.data_source.MemeDataSource
import com.mobilecampus.mastermeme.meme.domain.model.Meme
import com.mobilecampus.mastermeme.meme.domain.use_case.DeleteMemeUseCase

class DeleteMemeUseCaseImpl(
    private val dataSource: MemeDataSource
) : DeleteMemeUseCase {
    override suspend operator fun invoke(meme: Meme) {
        dataSource.deleteMeme(meme)
    }
}