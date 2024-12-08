package com.mobilecampus.mastermeme.meme.presentation.meme_list.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mobilecampus.mastermeme.meme.presentation.meme_list.MemeListScreen
import com.mobilecampus.mastermeme.meme.presentation.meme_list.MemeListViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data object MemeListNavigation

fun NavGraphBuilder.memeListDestination() {
    composable<MemeListNavigation> {
        val viewModel = koinViewModel<MemeListViewModel>()
        val state = viewModel.uiState.collectAsStateWithLifecycle().value

        MemeListScreen(
            state = state,
            onAction = {}
        )
    }
}