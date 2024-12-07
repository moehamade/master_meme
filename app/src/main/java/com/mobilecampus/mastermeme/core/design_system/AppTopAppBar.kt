package com.mobilecampus.mastermeme.core.design_system

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopAppBar(
    title: String,
    selectedItemsCount: Int,
    isDropdownMenuExpanded: Boolean,
    selectedSortOption: SortOption,
    onDropDownMenuClick: () -> Unit,
    onDropdownMenuDismiss: () -> Unit,
    onDropdownMenuItemClick: (SortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(text = if (selectedItemsCount > 0) selectedItemsCount.toString() else title)
        },
        modifier = modifier,
        navigationIcon = {
            if (selectedItemsCount > 0) {
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.Close, contentDescription = null)
                }
            }
        },
        actions = {
            if (selectedItemsCount > 0) {
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.Share, contentDescription = null)
                }
                IconButton(onClick = { /* doSomething() */ }) {
                    Icon(Icons.Filled.Delete, contentDescription = null)
                }
            } else {
                TextButton(onClick = onDropDownMenuClick) {
                    Text(selectedSortOption.displayName)
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                }
                DropdownMenu(
                    expanded = isDropdownMenuExpanded,
                    onDismissRequest = onDropdownMenuDismiss
                ) {
                    SortOption.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.displayName) },
                            onClick = { onDropdownMenuItemClick(option) }
                        )
                    }
                }
            }
        }
    )
}

enum class SortOption(val displayName: String) {
    FAVORITES_FIRST("Favorites First"),
    NEWEST_FIRST("Newest First")
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AppTopAppBarPreview() {
    val isDropdownMenuExpanded = remember { mutableStateOf(false) }
    AppTopAppBar(
        title = "Your memes",
        selectedItemsCount = 2,
        isDropdownMenuExpanded = isDropdownMenuExpanded.value,
        onDropdownMenuDismiss = { isDropdownMenuExpanded.value = false },
        onDropDownMenuClick = {},
        selectedSortOption = SortOption.FAVORITES_FIRST,
        onDropdownMenuItemClick = { /* Handle item click */ }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppCenterAlignedTopAppBar(
    title: String,
    showNavigationIcon: Boolean = false,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = title)
        },
        modifier = modifier,
        navigationIcon = {
            if (showNavigationIcon) {
                IconButton(onClick = {}) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            }
        },
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AppCenterAlignedTopAppBarPreview() {
    AppCenterAlignedTopAppBar(title = "New meme", showNavigationIcon = true)
}