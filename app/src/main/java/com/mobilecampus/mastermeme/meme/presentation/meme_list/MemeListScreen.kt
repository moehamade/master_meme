package com.mobilecampus.mastermeme.meme.presentation.meme_list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mobilecampus.mastermeme.meme.presentation.meme_list.components.MemeListTopAppBar
import com.mobilecampus.mastermeme.meme.presentation.meme_list.components.SortOption

@Composable
fun MemeListScreen() {
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
        Column(modifier = Modifier.padding(paddingValues)) {
            // Content of the screen
        }
    }
}