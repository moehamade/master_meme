package com.mobilecampus.mastermeme.meme.data.local.use_case

import com.mobilecampus.mastermeme.meme.domain.data_source.MemeDataSource
import com.mobilecampus.mastermeme.meme.domain.model.MemeItem
import com.mobilecampus.mastermeme.meme.domain.model.SortOption
import com.mobilecampus.mastermeme.meme.domain.use_case.GetMemesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetMemesUseCaseImpl(
    private val dataSource: MemeDataSource
) : GetMemesUseCase {
    override operator fun invoke(sortOption: Flow<SortOption>): Flow<List<MemeItem.ImageMeme>> {
        return combine(
            dataSource.getMemes(),
            sortOption
        ) { memes, option ->
            when (option) {
                SortOption.FAVORITES_FIRST -> memes.sortedWith(
                    compareByDescending<MemeItem.ImageMeme> { it.isFavorite }
                        .thenByDescending { it.createdAt }
                )
                SortOption.NEWEST_FIRST -> memes.sortedByDescending { it.createdAt }
            }
        }
    }
}
