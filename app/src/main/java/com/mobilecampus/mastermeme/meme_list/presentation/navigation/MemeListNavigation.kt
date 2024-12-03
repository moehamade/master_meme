package com.mobilecampus.mastermeme.meme_list.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mobilecampus.mastermeme.meme_list.presentation.MemeListScreen
import kotlinx.serialization.Serializable

@Serializable
data object MemeListNavigation

fun NavGraphBuilder.memeListDestination() {
    composable<MemeListNavigation>() {
        MemeListScreen()
    }
}