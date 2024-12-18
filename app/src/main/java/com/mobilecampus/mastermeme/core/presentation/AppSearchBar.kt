package com.mobilecampus.mastermeme.core.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobilecampus.mastermeme.R
import com.mobilecampus.mastermeme.ui.theme.LightLavender
import com.mobilecampus.mastermeme.ui.theme.MasterMemeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchableHeader(
    isSearchActive: Boolean,
    onSearchClick: () -> Unit,
    onSearchClose: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    searchQuery: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }

    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        if (isSearchActive) {
            DockedSearchBar(
                modifier = Modifier
                    .fillMaxSize()
                    .semantics { traversalIndex = 0f },
                inputField = {
                    TextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChanged,
                        placeholder = { Text("Search memes...") },
                        leadingIcon = {
                            IconButton(onClick = onSearchClose) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.surfaceDim
                                )
                            }
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChanged("") }) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Clear search",
                                        tint = MaterialTheme.colorScheme.surfaceDim
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            cursorColor = LightLavender,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        singleLine = true,
                    )
                },
                tonalElevation = 0.dp,
                expanded = isExpanded,
                onExpandedChange = { isExpanded = it },
                colors = SearchBarDefaults.colors(
                    containerColor = Color.Transparent,
                    dividerColor = MaterialTheme.colorScheme.outline,
                )
            ) {
                content()
            }
        } else {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.meme_list_choose_meme),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search"
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.meme_list_choose_meme_description),
                    modifier = Modifier.padding(top = 16.dp),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }

        }
    }
}

@Preview
@Composable
private fun AppSearchPreview() {
    var isActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    MasterMemeTheme {
        Column(
            modifier = Modifier.systemBarsPadding(),
        ) {
            SearchableHeader(
                isSearchActive = isActive,
                onSearchClick = {
                    isActive = true
                },
                onSearchClose = {
                    isActive = false
                },
                onSearchQueryChanged = {
                    searchQuery = it
                },
                searchQuery = searchQuery
            ) {}
        }


    }
}