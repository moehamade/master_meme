package com.mobilecampus.mastermeme.meme.presentation.screens.list.mvi

import com.mobilecampus.mastermeme.meme.domain.model.Meme
import com.mobilecampus.mastermeme.meme.domain.model.SortOption

sealed class MemeListState {
    data object Loading : MemeListState()
    data class Loaded(
        val memes: List<Meme>,
        val sortMode: SortOption = SortOption.FAVORITES_FIRST,
        val selectionMode: Boolean = false,
        val selectedMemes: Set<Meme> = setOf()
    ) : MemeListState()
    data class Error(val message: String) : MemeListState()
    data object Empty : MemeListState()
}