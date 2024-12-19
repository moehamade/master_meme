package com.mobilecampus.mastermeme.core.presentation

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.mobilecampus.mastermeme.ui.theme.MasterMemeTheme
import org.koin.compose.KoinContext

@Composable
fun MasterMemeApp() {
    KoinContext {
        MasterMemeTheme {
            Scaffold { innerPadding ->
                NavigationRoot(
                    innerPadding = innerPadding,
                    navController = rememberNavController()
                )
            }
        }
    }
}