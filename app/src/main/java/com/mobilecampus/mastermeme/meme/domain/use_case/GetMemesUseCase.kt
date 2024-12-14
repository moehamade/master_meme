package com.mobilecampus.mastermeme.meme.domain.use_case

import com.mobilecampus.mastermeme.meme.domain.model.MemeItem
import com.mobilecampus.mastermeme.meme.domain.model.SortOption
import kotlinx.coroutines.flow.Flow

// First, let's modify the use case interface to accept the sort option
interface GetMemesUseCase {
    operator fun invoke(sortOption: Flow<SortOption>): Flow<List<MemeItem.ImageMeme>>
}