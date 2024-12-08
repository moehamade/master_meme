package com.mobilecampus.mastermeme.meme.presentation.screens.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mobilecampus.mastermeme.R
import com.mobilecampus.mastermeme.meme.domain.model.SortOption
import com.mobilecampus.mastermeme.meme.presentation.screens.list.components.MemeListTopAppBar
import com.mobilecampus.mastermeme.meme.presentation.screens.list.mvi.MemeListAction
import com.mobilecampus.mastermeme.meme.presentation.screens.list.mvi.MemeListState

@Composable
fun MemeListScreen(
    state: MemeListState,
    onAction : (MemeListAction) -> Unit
) {
    var isOpen by remember { mutableStateOf(false) }
    var selectedSortOption by remember { mutableStateOf(SortOption.FAVORITES_FIRST) }
    var selectedItemsCount by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            MemeListTopAppBar(
                selectedItemsCount = selectedItemsCount,
                isDropdownMenuExpanded = isOpen,
                selectedSortOption = selectedSortOption,
                onDropDownMenuClick = {
                    isOpen = !isOpen
                },
                onDropdownMenuDismiss = {
                    isOpen = false
                },
                onDropdownMenuItemClick = { option ->
                    selectedSortOption = option
                    isOpen = false
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton({}, modifier = Modifier.padding(bottom = 16.dp)) {
                Icon(Icons.Filled.Add, contentDescription = null)
            }
        }
    ) { paddingValues ->
        EmptyMemeListState(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
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