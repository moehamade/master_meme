package com.mobilecampus.mastermeme.meme.presentation.meme_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobilecampus.mastermeme.meme.domain.use_case.DeleteMemeUseCase
import com.mobilecampus.mastermeme.meme.domain.use_case.GetMemesUseCase
import com.mobilecampus.mastermeme.meme.domain.use_case.GetTemplatesUseCase
import com.mobilecampus.mastermeme.meme.domain.use_case.ToggleFavoriteUseCase
import com.mobilecampus.mastermeme.meme.presentation.screens.list.mvi.MemeListAction
import com.mobilecampus.mastermeme.meme.presentation.screens.list.mvi.MemeListState
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