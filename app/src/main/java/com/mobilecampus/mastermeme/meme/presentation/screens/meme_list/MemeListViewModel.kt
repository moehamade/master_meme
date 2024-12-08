package com.mobilecampus.mastermeme.meme.presentation.screens.meme_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobilecampus.mastermeme.meme.domain.model.Meme
import com.mobilecampus.mastermeme.meme.domain.model.SortOption
import com.mobilecampus.mastermeme.meme.domain.use_case.DeleteMemeUseCase
import com.mobilecampus.mastermeme.meme.domain.use_case.GetMemesUseCase
import com.mobilecampus.mastermeme.meme.domain.use_case.GetTemplatesUseCase
import com.mobilecampus.mastermeme.meme.domain.use_case.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MemeListViewModel(
     private val getMemesUseCase: GetMemesUseCase,
     private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
     private val deleteMemesUseCase: DeleteMemeUseCase,
     private val getTemplatesUseCase: GetTemplatesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(MemeListState.Loading)
    val uiState = _uiState.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MemeListState.Loading
    )

    fun onAction(action: MemeListAction) {
        viewModelScope.launch {
            when (action) {
                MemeListAction.OnCreateMemeClick -> {

                }
            }
        }
    }

}

sealed interface MemeListAction {
    data object OnCreateMemeClick : MemeListAction
}

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