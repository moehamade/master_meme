package com.mobilecampus.mastermeme.meme.presentation.meme_creation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mobilecampus.mastermeme.meme.presentation.meme_creation.MemeCreationScreen
import kotlinx.serialization.Serializable

@Serializable
data object MemeCreationNavigation

fun NavController.navigateMemeCreation() = navigate(MemeCreationNavigation)


fun NavGraphBuilder.memeCreationDestination() {
    composable<MemeCreationNavigation>() {
        MemeCreationScreen()
    }
}