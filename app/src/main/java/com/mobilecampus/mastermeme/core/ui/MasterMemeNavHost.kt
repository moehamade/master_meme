package com.mobilecampus.mastermeme.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.mobilecampus.mastermeme.meme_creation.presentation.navigation.memeCreationDestination
import com.mobilecampus.mastermeme.meme_list.presentation.navigation.MemeListNavigation
import com.mobilecampus.mastermeme.meme_list.presentation.navigation.memeListDestination

@Composable
fun MasterMemeNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = MemeListNavigation,
        modifier = modifier
    ) {
        memeListDestination()
        memeCreationDestination()
    }
}