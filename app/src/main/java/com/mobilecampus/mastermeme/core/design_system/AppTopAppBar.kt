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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(text = "2")
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = {}) {
                Icon(Icons.Filled.Close, contentDescription = null)
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(Icons.Filled.Share, contentDescription = null)
            }
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(Icons.Filled.Delete, contentDescription = null)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestAppBar(modifier: Modifier = Modifier) {
    TopAppBar(
        title = {
            Text(text = "Your memes")
        },
        modifier = modifier,
        actions = {
            TextButton({}) {
                Text("Favorites First")
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(expanded = true, onDismissRequest = {}) {
                DropdownMenuItem(text = {
                    Text("Item 1")
                },onClick = {})
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppCenterAlignedTopAppBar(modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = "New meme")
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = {}) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AppTopBarPreview() {
    AppTopBar()
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AppCenterAlignedTopAppBarPreview() {
    AppCenterAlignedTopAppBar()
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TestAppBarPreview() {
    TestAppBar()
}