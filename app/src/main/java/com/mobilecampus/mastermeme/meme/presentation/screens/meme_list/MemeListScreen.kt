package com.mobilecampus.mastermeme.meme.presentation.screens.meme_list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mobilecampus.mastermeme.R
import com.mobilecampus.mastermeme.core.presentation.AnimatedSearchableHeader
import com.mobilecampus.mastermeme.core.presentation.design_system.AppIcons
import com.mobilecampus.mastermeme.meme.domain.model.MemeItem
import com.mobilecampus.mastermeme.meme.presentation.screens.meme_list.components.AnimatedTemplateGrid
import com.mobilecampus.mastermeme.meme.presentation.screens.meme_list.components.MemeListTopAppBar
import com.mobilecampus.mastermeme.meme.presentation.screens.meme_list.components.UserMemeGrid
import com.mobilecampus.mastermeme.ui.theme.White
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemeListScreenRoot(
    onOpenEditorScreen: (id: Int) -> Unit,
) {
    val viewModel = koinViewModel<MemeListViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is MemeListScreenEvent.NavigateToEditor -> {
                    onOpenEditorScreen(event.id)
                }

                is MemeListScreenEvent.ShowError -> {
                    // TODO: Handle Error
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
    val gridScrollState = rememberLazyGridState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = state.isSearchActive)

    Scaffold(
        topBar = {
            MemeListTopAppBar(
                selectedItemsCount = state.selectedMemesCount,
                isDropdownMenuExpanded = isDropdownMenuExpanded,
                selectedSortOption = state.sortOption,
                onDropDownMenuClick = { isDropdownMenuExpanded = true },
                onDropdownMenuDismiss = { isDropdownMenuExpanded = false },
                onCancelSelection = { onAction(MemeListAction.DisableSelectionMode) },
                onDeleteClick = { onAction(MemeListAction.SetDeleteDialogVisible(true)) },
                onDropdownMenuItemClick = { option ->
                    onAction(MemeListAction.UpdateSortOption(option))
                    isDropdownMenuExpanded = false
                },
                onShareClick = { onAction(MemeListAction.ShareSelectedMemes) }
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
        Box(modifier = Modifier.fillMaxSize()) {
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
                            state = gridScrollState,
                            onMemeTap = { meme ->
                                if (state.isSelectionModeActive) {
                                    onAction(MemeListAction.ToggleMemeSelection(meme.id!!))
                                }
                            },
                            onFavoriteToggle = { meme ->
                                onAction(MemeListAction.ToggleFavorite(meme))
                            },
                            modifier = Modifier.padding(
                                start = paddingValues.calculateLeftPadding(LayoutDirection.Ltr),
                                end = paddingValues.calculateRightPadding(LayoutDirection.Ltr),
                                top = paddingValues.calculateTopPadding(),
                            ),
                            isSelectionMode = state.isSelectionModeActive,
                            selectedMemes = state.selectedMemesIds,
                            onSelectionToggle = { meme, _ ->
                                onAction(MemeListAction.ToggleMemeSelection(meme.id!!))
                            },
                            sortOption = state.sortOption
                        )
                    }
                }
            }

            if (state.isBottomSheetVisible) {
                ModalBottomSheet(
                    onDismissRequest = {
                        onAction(MemeListAction.SetBottomSheetVisibility(false))
                    },
                    sheetState = sheetState,
                    contentWindowInsets = { WindowInsets(0) },
                    properties = ModalBottomSheetProperties(
                        shouldDismissOnBackPress = true
                    )
                ) {
                    TemplateSelectionContent(
                        templates = state.filteredTemplates,
                        onTemplateSelected = { template ->
                            onAction(MemeListAction.SetBottomSheetVisibility(false))
                            onAction(MemeListAction.OpenTemplateEditor(template.resourceId))
                        },
                        isSearchActive = state.isSearchActive,
                        searchQuery = state.searchQuery,
                        onSearchActiveChange = { isActive ->
                            onAction(MemeListAction.SetSearchActive(isActive))
                        },
                        onSearchQueryChange = { query ->
                            onAction(MemeListAction.UpdateSearchQuery(query))
                        }
                    )
                }
            }

            AnimatedVisibility(
                visible = state.isDeleteDialogVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                DeleteMemesDialog(
                    selectedCount = state.selectedMemesCount,
                    onConfirm = {
                        onAction(MemeListAction.DeleteSelectedMemes(state.selectedMemesIds))
                    },
                    onCancel = {
                        onAction(MemeListAction.SetDeleteDialogVisible(false))
                    },
                    onDismiss = {
                        onAction(MemeListAction.SetDeleteDialogVisible(false))
                        onAction(MemeListAction.DisableSelectionMode)
                    }
                )
            }
        }
    }
}

@Composable
fun TemplateSelectionContent(
    templates: List<MemeItem.Template>,
    onTemplateSelected: (MemeItem.Template) -> Unit,
    isSearchActive: Boolean,
    searchQuery: String,
    onSearchActiveChange: (Boolean) -> Unit,
    onSearchQueryChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .imePadding()
    ) {
        AnimatedSearchableHeader(
            isSearchActive = isSearchActive,
            onSearchClick = { onSearchActiveChange(true) },
            onSearchClose = {
                onSearchActiveChange(false)
                onSearchQueryChange("")
            },
            onSearchQueryChanged = onSearchQueryChange,
            searchQuery = searchQuery,
            content = {
                AnimatedVisibility(
                    visible = templates.isNotEmpty(),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        Text(
                            text = if (searchQuery.isNotEmpty()) {
                                "${templates.size} templates found"
                            } else {
                                "${templates.size} templates available"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        AnimatedTemplateGrid(
                            modifier = Modifier.fillMaxSize(),
                            templates = templates,
                            onTemplateClick = onTemplateSelected,
                            columns = 2,
                            contentPadding = PaddingValues(0.dp)
                        )
                    }
                }
                AnimatedVisibility(
                    visible = templates.isEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No templates found :(",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                }
            }
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

@Composable
fun DeleteMemesDialog(
    selectedCount: Int,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Delete $selectedCount ${if (selectedCount == 1) "meme" else "memes"}?",
                style = MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.onSurface)
            )
        },
        text = {
            Text(
                text = "You will not be able to restore them. If you're fine with that, press 'Delete'.",
                style = MaterialTheme.typography.bodyMedium.copy(color = White)
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.surfaceDim
                )
            ) {
                Text(
                    "Delete",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.surfaceDim)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onCancel, colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.surfaceDim

                )
            ) {
                Text(
                    "Cancel",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.surfaceDim)
                )
            }
        }
    )
}