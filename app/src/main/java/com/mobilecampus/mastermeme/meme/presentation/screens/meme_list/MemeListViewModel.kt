package com.mobilecampus.mastermeme.meme.presentation.screens.meme_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobilecampus.mastermeme.meme.domain.model.Meme
import com.mobilecampus.mastermeme.meme.domain.model.SortOption
import com.mobilecampus.mastermeme.meme.domain.use_case.DeleteMemeUseCase
import com.mobilecampus.mastermeme.meme.domain.use_case.GetMemesUseCase
import com.mobilecampus.mastermeme.meme.domain.use_case.GetTemplatesUseCase
import com.mobilecampus.mastermeme.meme.domain.use_case.ToggleFavoriteUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MemeListViewModel(
    private val getMemesUseCase: GetMemesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val deleteMemesUseCase: DeleteMemeUseCase,
    private val getTemplatesUseCase: GetTemplatesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<MemeListState>(MemeListState.Loading)
    val uiState = _uiState
        .onStart { loadMemes() }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MemeListState.Loading
        )

    // Event channel for one-time UI events
    private val eventChannel = Channel<MemeListScreenEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: MemeListAction) {
        viewModelScope.launch {
            when (action) {
                is MemeListAction.MemeClickAction -> {
                    eventChannel.send(
                        MemeListScreenEvent.OnGotoEditorScreen(action.id)
                    )
                }
            }
        }
    }

    private fun loadMemes() {
        viewModelScope.launch {
            try {
                _uiState.update { MemeListState.Loading }
                delay(2000)
                getMemesUseCase(SortOption.FAVORITES_FIRST).collect { memes ->
                    _uiState.value = if (memes.isEmpty()) {
                        MemeListState.Empty
                    } else {
                        MemeListState.Loaded(
                            memes = memes,
                            sortMode = SortOption.FAVORITES_FIRST
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { MemeListState.Error("Failed to load memes") }
            }
        }
    }

sealed interface MemeListAction {
    data class MemeClickAction(val id: String) : MemeListAction
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
}

sealed interface MemeListScreenEvent {
    data class OnGotoEditorScreen(val id: String): MemeListScreenEvent
}