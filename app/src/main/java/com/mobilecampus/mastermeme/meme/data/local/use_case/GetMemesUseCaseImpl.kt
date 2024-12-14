package com.mobilecampus.mastermeme.meme.data.local.use_case

import com.mobilecampus.mastermeme.meme.domain.data_source.MemeDataSource
import com.mobilecampus.mastermeme.meme.domain.model.MemeItem
import com.mobilecampus.mastermeme.meme.domain.model.SortOption
import com.mobilecampus.mastermeme.meme.domain.use_case.GetMemesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetMemesUseCaseImpl(
    private val dataSource: MemeDataSource
) : GetMemesUseCase {
    // Notice we removed the sortOption parameter since sorting is now handled at collection time
    override operator fun invoke(): Flow<List<MemeItem.ImageMeme>> {
        return dataSource.getMemes()
    }
}
