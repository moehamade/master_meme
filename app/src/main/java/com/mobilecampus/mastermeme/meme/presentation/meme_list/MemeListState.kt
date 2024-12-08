package com.mobilecampus.mastermeme.meme.presentation.meme_list

import com.mobilecampus.mastermeme.meme.domain.model.Meme
import com.mobilecampus.mastermeme.meme.domain.model.SortOption

sealed class MemeListState {
    object Loading : MemeListState()
    data class Loaded(
        val memes: List<Meme>,
        val sortMode: SortOption = SortOption.FAVORITES_FIRST,
        val selectionMode: Boolean = false,
        val selectedMemes: Set<Meme> = setOf()
    ) : MemeListState()
    data class Error(val message: String) : MemeListState()
    object Empty : MemeListState()
}