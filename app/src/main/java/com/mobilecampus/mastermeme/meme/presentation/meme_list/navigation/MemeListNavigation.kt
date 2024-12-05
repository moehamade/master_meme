package com.mobilecampus.mastermeme.meme.presentation.meme_list.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mobilecampus.mastermeme.meme.presentation.meme_list.MemeListScreen
import kotlinx.serialization.Serializable

@Serializable
data object MemeListNavigation

fun NavGraphBuilder.memeListDestination() {
    composable<MemeListNavigation> {
        MemeListScreen()
    }
}