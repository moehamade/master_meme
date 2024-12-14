package com.mobilecampus.mastermeme.meme.presentation.screens.meme_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobilecampus.mastermeme.meme.domain.model.MemeItem
import com.mobilecampus.mastermeme.meme.domain.model.SortOption
import com.mobilecampus.mastermeme.meme.domain.use_case.DeleteMemeUseCase
import com.mobilecampus.mastermeme.meme.domain.use_case.GetMemesUseCase
import com.mobilecampus.mastermeme.meme.domain.use_case.GetTemplatesUseCase
import com.mobilecampus.mastermeme.meme.domain.use_case.ToggleFavoriteUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// State.kt
// Contains all state-related classes and interfaces
data class MemeListScreenState(
    val memes: List<MemeItem.ImageMeme> = emptyList(),
    val templates: List<MemeItem.Template> = emptyList(),
    val filteredTemplates: List<MemeItem.Template> = emptyList(),
    val loadingState: LoadingState = LoadingState.Loading,
    val sortOption: SortOption = SortOption.FAVORITES_FIRST,
    val isSelectionModeActive: Boolean = false,
    val selectedMemes: Set<Int> = emptySet(),
    val isBottomSheetVisible: Boolean = false,
    val templateSearchQuery: String = "",
) {
    // Helper properties for UI logic
    val selectedMemesCount: Int get() = selectedMemes.size
    val isEmpty: Boolean get() = loadingState == LoadingState.Success && memes.isEmpty()

    // Get sorted memes based on current sort option
    val sortedMemes: List<MemeItem.ImageMeme>
        get() = when (sortOption) {
            SortOption.FAVORITES_FIRST -> memes.sortedWith(
                compareByDescending<MemeItem.ImageMeme> { it.isFavorite }
                    .thenByDescending { it.createdAt }
            )
            SortOption.NEWEST_FIRST -> memes.sortedByDescending { it.createdAt }
        }
}

sealed class LoadingState {
    object Loading : LoadingState()
    object Success : LoadingState()
    data class Error(val message: String) : LoadingState()
}

// Actions.kt
// Contains all possible actions that can be triggered from the UI
sealed interface MemeListAction {
    // Navigation actions
    data class OpenMemeEditor(val memeId: Int) : MemeListAction
    data class OpenTemplateEditor(val templateId: Int) : MemeListAction

    // Meme management
    data class ToggleFavorite(val meme: MemeItem.ImageMeme) : MemeListAction
    data class DeleteSelectedMemes(val ids: Set<Int>) : MemeListAction

    // Selection mode
    data class ToggleMemeSelection(val memeId: Int) : MemeListAction
    object EnableSelectionMode : MemeListAction
    object DisableSelectionMode : MemeListAction
    object ClearSelection : MemeListAction

    // Template and bottom sheet
    data class UpdateTemplateSearch(val query: String) : MemeListAction
    data class SetBottomSheetVisibility(val visible: Boolean) : MemeListAction

    // Sorting
    data class UpdateSortOption(val option: SortOption) : MemeListAction
}

// Events.kt
// Contains all events that can be emitted by the ViewModel
sealed interface MemeListScreenEvent {
    data class NavigateToEditor(val id: Int) : MemeListScreenEvent
    data class ShowError(val message: String) : MemeListScreenEvent
}

// ViewModel.kt
class MemeListViewModel(
    private val getMemesUseCase: GetMemesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val deleteMemesUseCase: DeleteMemeUseCase,
    private val getTemplatesUseCase: GetTemplatesUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(MemeListScreenState())
    val state = _state.asStateFlow()

    private val eventChannel = Channel<MemeListScreenEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        loadMemes()
        loadTemplates()
    }

    private fun loadMemes() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(loadingState = LoadingState.Loading) }
                getMemesUseCase(_state.value.sortOption).collect { memes ->
                    _state.update { currentState ->
                        currentState.copy(
                            memes = memes,
                            loadingState = LoadingState.Success
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(loadingState = LoadingState.Error("Failed to load memes"))
                }
            }
        }
    }

    private fun loadTemplates() {
        viewModelScope.launch {
            try {
                val templates = getTemplatesUseCase()
                _state.update { currentState ->
                    currentState.copy(
                        templates = templates,
                        filteredTemplates = templates
                    )
                }
            } catch (e: Exception) {
                eventChannel.send(
                    MemeListScreenEvent.ShowError("Failed to load templates")
                )
            }
        }
    }

    fun onAction(action: MemeListAction) {

        when (action) {
            is MemeListAction.OpenMemeEditor -> navigateToEditor(action.memeId)
            is MemeListAction.OpenTemplateEditor -> navigateToEditor(action.templateId)
            is MemeListAction.ToggleFavorite -> toggleFavorite(action.meme)
            is MemeListAction.DeleteSelectedMemes -> deleteSelectedMemes(action.ids)
            is MemeListAction.ToggleMemeSelection -> toggleMemeSelection(action.memeId)
            is MemeListAction.EnableSelectionMode -> enableSelectionMode()
            is MemeListAction.DisableSelectionMode -> disableSelectionMode()
            is MemeListAction.ClearSelection -> clearSelection()
            is MemeListAction.UpdateTemplateSearch -> updateTemplateSearch(action.query)
            is MemeListAction.SetBottomSheetVisibility -> updateBottomSheetVisibility(action.visible)
            is MemeListAction.UpdateSortOption -> updateSortOption(action.option)
        }
    }

    private fun navigateToEditor(id: Int) {
        viewModelScope.launch {
            eventChannel.send(MemeListScreenEvent.NavigateToEditor(id))
        }
    }

    private fun toggleFavorite(meme: MemeItem.ImageMeme) {
        viewModelScope.launch {
            try {
                toggleFavoriteUseCase(meme.id!!)
            } catch (e: Exception) {
                eventChannel.send(
                    MemeListScreenEvent.ShowError("Failed to toggle favorite")
                )
            }
        }
    }

    private fun deleteSelectedMemes(ids: Set<Int>) {
//        viewModelScope.launch {
//            try {
//                ids.forEach { deleteMemesUseCase(it) }
//                clearSelection()
//            } catch (e: Exception) {
//                eventChannel.send(
//                    MemeListScreenEvent.ShowError("Failed to delete memes")
//                )
//            }
//        }
    }

    private fun toggleMemeSelection(memeId: Int) {
        _state.update { currentState ->
            val newSelectedMemes = currentState.selectedMemes.toMutableSet()
            if (memeId in newSelectedMemes) {
                newSelectedMemes.remove(memeId)
            } else {
                newSelectedMemes.add(memeId)
            }

            currentState.copy(
                selectedMemes = newSelectedMemes,
                isSelectionModeActive = newSelectedMemes.isNotEmpty()
            )
        }
    }

    private fun enableSelectionMode() {
        _state.update { it.copy(isSelectionModeActive = true) }
    }

    private fun disableSelectionMode() {
        _state.update { it.copy(isSelectionModeActive = false) }
        clearSelection()
    }

    private fun clearSelection() {
        _state.update { it.copy(selectedMemes = emptySet()) }
    }

    private fun updateTemplateSearch(query: String) {
        _state.update { currentState ->
            val filtered = if (query.isEmpty()) {
                currentState.templates
            } else {
                currentState.templates.filter {
                    it.description?.contains(query, ignoreCase = true) == true
                }
            }

            currentState.copy(
                templateSearchQuery = query,
                filteredTemplates = filtered
            )
        }
    }

    private fun updateBottomSheetVisibility(visible: Boolean) {
        _state.update { it.copy(isBottomSheetVisible = visible) }
    }

    private fun updateSortOption(option: SortOption) {
        _state.update { it.copy(sortOption = option) }
        loadMemes() // Reload memes with new sort option
    }
}