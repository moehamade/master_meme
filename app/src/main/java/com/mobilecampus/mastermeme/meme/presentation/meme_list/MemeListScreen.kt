package com.mobilecampus.mastermeme.meme.presentation.meme_list

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mobilecampus.mastermeme.core.design_system.AppTopAppBar
import com.mobilecampus.mastermeme.core.design_system.SortOption

@Composable
fun MemeListScreen(modifier: Modifier = Modifier) {
    var isOpen by remember { mutableStateOf(false) }
    var selectedSortOption by remember { mutableStateOf(SortOption.FAVORITES_FIRST) }

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Meme List",
                selectedItemsCount = 0,
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
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Content of the screen
        }
    }
}