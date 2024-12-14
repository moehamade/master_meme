package com.mobilecampus.mastermeme.meme.domain.use_case

import com.mobilecampus.mastermeme.meme.domain.model.MemeItem
import com.mobilecampus.mastermeme.meme.domain.model.SortOption
import kotlinx.coroutines.flow.Flow

interface GetMemesUseCase {
    operator fun invoke(sortOption: SortOption): Flow<List<MemeItem.ImageMeme>>
}