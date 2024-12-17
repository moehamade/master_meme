package com.mobilecampus.mastermeme.meme.presentation.screens.meme_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobilecampus.mastermeme.meme.domain.model.MemeItem
import com.mobilecampus.mastermeme.meme.domain.model.SortOption
import com.mobilecampus.mastermeme.meme.domain.use_case.DeleteMemeUseCase
import com.mobilecampus.mastermeme.meme.domain.use_case.GetMemesUseCase
import com.mobilecampus.mastermeme.meme.domain.use_case.GetTemplatesUseCase
import com.mobilecampus.mastermeme.meme.domain.use_case.ShareMemesUseCase
import com.mobilecampus.mastermeme.meme.domain.use_case.ToggleFavoriteUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
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
    val selectedMemesIds: Set<Int> = emptySet(),
    val isBottomSheetVisible: Boolean = false,
    val templateSearchQuery: String = "",
    val isDeleteDialogVisible: Boolean = false, // New state
) {
    // Helper properties for UI logic
    val selectedMemesCount: Int get() = selectedMemesIds.size
    val isEmpty: Boolean get() = loadingState == LoadingState.Success && memes.isEmpty()
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

    data class SetDeleteDialogVisible(val visible: Boolean) : MemeListAction

    object ShareSelectedMemes : MemeListAction


}

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
    private val getTemplatesUseCase: GetTemplatesUseCase,
    private val shareMemesUseCase: ShareMemesUseCase,
    ) : ViewModel() {
    private val sortOptionFlow = MutableStateFlow(SortOption.FAVORITES_FIRST)

    private val _state = MutableStateFlow(MemeListScreenState())
    val state = _state
        .onStart {
            loadMemes()
            loadTemplates()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MemeListScreenState()
        )

    private val eventChannel = Channel<MemeListScreenEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    private fun loadMemes() {
        viewModelScope.launch {
            _state.update { it.copy(loadingState = LoadingState.Loading) }

            // Pass the sort option flow to the use case
            getMemesUseCase(sortOptionFlow)
                .collectLatest { sortedMemes ->
                    _state.update { currentState ->
                        currentState.copy(
                            memes = sortedMemes,
                            loadingState = LoadingState.Success
                        )
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
            is MemeListAction.SetDeleteDialogVisible -> setDeleteDialogVisible(action.visible)
            is MemeListAction.ToggleMemeSelection -> toggleMemeSelection(action.memeId)
            is MemeListAction.EnableSelectionMode -> enableSelectionMode()
            is MemeListAction.DisableSelectionMode -> disableSelectionMode()
            is MemeListAction.ClearSelection -> clearSelection()
            is MemeListAction.UpdateTemplateSearch -> updateTemplateSearch(action.query)
            is MemeListAction.SetBottomSheetVisibility -> updateBottomSheetVisibility(action.visible)
            is MemeListAction.UpdateSortOption -> updateSortOption(action.option)
            is MemeListAction.ShareSelectedMemes -> shareSelectedMemes()
        }
    }

    private fun shareSelectedMemes() {
        viewModelScope.launch {
            try {
                shareMemesUseCase(state.value.selectedMemesIds)
                disableSelectionMode()
            } catch (e: Exception) {
                eventChannel.send(
                    MemeListScreenEvent.ShowError("Failed to share memes: ${e.message}")
                )
            }
        }
    }

    private fun setDeleteDialogVisible(visible: Boolean) {
        _state.update { it.copy(isDeleteDialogVisible = visible) }
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
        viewModelScope.launch {
            try {
                _state.update { it.copy(loadingState = LoadingState.Loading) }
                deleteMemesUseCase(ids)

                clearSelection()
                disableSelectionMode()
                setDeleteDialogVisible(false) // Hide dialog after successful deletion

                _state.update { it.copy(loadingState = LoadingState.Success) }
            } catch (e: Exception) {
                _state.update { it.copy(loadingState = LoadingState.Success) }
                eventChannel.send(
                    MemeListScreenEvent.ShowError("Failed to delete memes: ${e.message}")
                )
            }
        }
    }

    private fun toggleMemeSelection(memeId: Int) {
        _state.update { currentState ->
            val newSelectedMemes = currentState.selectedMemesIds.toMutableSet()
            if (memeId in newSelectedMemes) {
                newSelectedMemes.remove(memeId)
            } else {
                newSelectedMemes.add(memeId)
            }

            currentState.copy(
                selectedMemesIds = newSelectedMemes,
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
        _state.update { it.copy(selectedMemesIds = emptySet()) }
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
        viewModelScope.launch {
            sortOptionFlow.emit(option)
            _state.update { it.copy(sortOption = option) }
        }
    }
}