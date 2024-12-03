package com.mobilecampus.mastermeme.meme_creation.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mobilecampus.mastermeme.meme_creation.presentation.MemeCreationScreen
import kotlinx.serialization.Serializable

@Serializable
data object MemeCreationNavigation

fun NavController.navigateMemeCreation() = navigate(MemeCreationNavigation)


fun NavGraphBuilder.memeCreationDestination() {
    composable<MemeCreationNavigation>() {
        MemeCreationScreen()
    }
}