package com.mobilecampus.mastermeme.meme.data.local.use_case

import com.mobilecampus.mastermeme.meme.domain.data_source.MemeDataSource
import com.mobilecampus.mastermeme.meme.domain.use_case.GetTemplatesUseCase

class GetTemplatesUseCaseImpl(
    private val dataSource: MemeDataSource
) : GetTemplatesUseCase {
    override suspend operator fun invoke(): List<String> {
        return dataSource.getTemplates()
    }
}