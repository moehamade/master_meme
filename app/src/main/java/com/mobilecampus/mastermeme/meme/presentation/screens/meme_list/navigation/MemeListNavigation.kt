package com.mobilecampus.mastermeme.meme.presentation.screens.meme_list.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mobilecampus.mastermeme.core.presentation.NavGraph
import com.mobilecampus.mastermeme.meme.presentation.screens.meme_list.MemeListScreenRoot

fun NavGraphBuilder.memeListDestination() {
    composable<NavGraph.MemeList> {
        MemeListScreenRoot()
    }
}