package com.mobilecampus.mastermeme.meme.presentation.screens.meme_list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection.Companion.Ltr
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mobilecampus.mastermeme.R
import com.mobilecampus.mastermeme.core.presentation.design_system.AppIcons
import com.mobilecampus.mastermeme.core.presentation.util.ObserveAsEvents
import com.mobilecampus.mastermeme.meme.domain.model.MemeItem
import com.mobilecampus.mastermeme.meme.domain.model.SortOption
import com.mobilecampus.mastermeme.meme.presentation.screens.meme_list.components.MemeListTopAppBar
import com.mobilecampus.mastermeme.meme.presentation.screens.meme_list.components.TemplateGrid
import com.mobilecampus.mastermeme.meme.presentation.screens.meme_list.components.UserMemeGrid
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemeListScreenRoot(
    onOpenEditorScreen: (id: Int) -> Unit,
) {
    val viewModel = koinViewModel<MemeListViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle().value

    // Handle events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is MemeListScreenEvent.NavigateToEditor -> {
                    onOpenEditorScreen(event.id)
                }
                is MemeListScreenEvent.ShowError -> {
                    // Show error using your preferred method (Snackbar, Toast, etc.)
                }
            }
        }
    }

    MemeListScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemeListScreen(
    state: MemeListScreenState,
    onAction: (MemeListAction) -> Unit
) {
    var isDropdownMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            MemeListTopAppBar(
                selectedItemsCount = state.selectedMemesCount,
                isDropdownMenuExpanded = isDropdownMenuExpanded,
                selectedSortOption = state.sortOption,
                onDropDownMenuClick = { isDropdownMenuExpanded = true },
                onDropdownMenuDismiss = { isDropdownMenuExpanded = false },
                onDropdownMenuItemClick = { option ->
                    onAction(MemeListAction.UpdateSortOption(option))
                    isDropdownMenuExpanded = false
                }
            )
        },
        floatingActionButton = {
            if (!state.isSelectionModeActive) {
                FloatingActionButton(
                    onClick = { onAction(MemeListAction.SetBottomSheetVisibility(true)) }
                ) {
                    Icon(AppIcons.add, contentDescription = null)
                }
            }
        }
    ) { paddingValues ->
        when (state.loadingState) {
            LoadingState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .wrapContentSize()
                )
            }
            is LoadingState.Error -> {
                // You can create a custom error state component
                Text(
                    text = state.loadingState.message,
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .wrapContentSize()
                )
            }
            LoadingState.Success -> {
                if (state.isEmpty) {
                    EmptyMemeListState(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                    )
                } else {
                    UserMemeGrid(
                        memes = state.memes,
                        onMemeTap = { meme ->
                            if (state.isSelectionModeActive) {
                                onAction(MemeListAction.ToggleMemeSelection(meme.id!!))
                            } else {
                                onAction(MemeListAction.OpenMemeEditor(meme.id!!))
                            }
                        },
                        onFavoriteToggle = { meme ->
                            onAction(MemeListAction.ToggleFavorite(meme))
                        },
                        modifier = Modifier
                            .padding(
                                start = paddingValues.calculateLeftPadding(LayoutDirection.Ltr),
                                end = paddingValues.calculateRightPadding(LayoutDirection.Ltr),
                                top = paddingValues.calculateTopPadding(),
                            )
                            .fillMaxSize(),
                        isSelectionMode = state.isSelectionModeActive,
                        selectedMemes = state.selectedMemes,
                        onSelectionToggle = { meme, isSelected ->
                            onAction(MemeListAction.ToggleMemeSelection(meme.id!!))
                        }
                    )
                }
            }
        }

        if (state.isBottomSheetVisible) {
            ModalBottomSheet(
                onDismissRequest = {
                    onAction(MemeListAction.SetBottomSheetVisibility(false))
                },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                TemplateSelectionContent(
                    templates = state.filteredTemplates,
                    onTemplateSelected = { template ->
                        onAction(MemeListAction.OpenTemplateEditor(template.resourceId))
                        onAction(MemeListAction.SetBottomSheetVisibility(false))
                    }
                )
            }
        }
    }
}

// Template bottom sheet content
@Composable
private fun TemplateSelectionContent(
    templates: List<MemeItem.Template>,
    onTemplateSelected: (MemeItem.Template) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.meme_list_choose_meme),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.meme_list_choose_meme_description),
            modifier = Modifier.padding(vertical = 32.dp),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )

        TemplateGrid(
            templates = templates,
            onTemplateClick = onTemplateSelected,
            columns = 2,
            contentPadding = PaddingValues(0.dp)
        )
    }
}

@Composable
fun EmptyMemeListState(modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_empty_meme),
            contentDescription = "Empty state"
        )
        Text(
            text = stringResource(R.string.tap_button_to_create_your_first_meme),
            modifier = Modifier.padding(top = 34.dp),
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center
            )
        )
    }
}