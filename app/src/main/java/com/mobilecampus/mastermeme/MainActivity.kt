package com.mobilecampus.mastermeme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.mobilecampus.mastermeme.core.presentation.NavigationRoot
import com.mobilecampus.mastermeme.ui.theme.MasterMemeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
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
}