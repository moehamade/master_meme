package com.mobilecampus.mastermeme.meme.data.local.use_case

import com.mobilecampus.mastermeme.meme.domain.data_source.MemeDataSource
import com.mobilecampus.mastermeme.meme.domain.model.Meme
import com.mobilecampus.mastermeme.meme.domain.model.SortOption
import com.mobilecampus.mastermeme.meme.domain.use_case.GetMemesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetMemesUseCaseImpl(
    private val dataSource: MemeDataSource
) : GetMemesUseCase {
    override operator fun invoke(sortOption: SortOption): Flow<List<Meme>> {
        return dataSource.getMemes().map { memes ->
            when (sortOption) {
                SortOption.FAVORITES_FIRST -> {
                    memes.sortedWith(
                        compareByDescending<Meme> { it.isFavorite }
                            .thenByDescending { it.createdAt }
                    )
                }
                SortOption.NEWEST_FIRST -> {
                    memes.sortedByDescending { it.createdAt }
                }
            }
        }
    }
}