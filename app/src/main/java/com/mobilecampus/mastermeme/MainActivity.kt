package com.mobilecampus.mastermeme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.mobilecampus.mastermeme.core.ui.MasterMemeNavHost
import com.mobilecampus.mastermeme.meme.data.di.memeDataModule
import com.mobilecampus.mastermeme.meme.domain.di.domainModule
import com.mobilecampus.mastermeme.meme.presentation.di.presentationModule
import com.mobilecampus.mastermeme.ui.theme.MasterMemeTheme
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            MasterMemeTheme {
                MasterMemeNavHost(
                    modifier = Modifier
                )
            }
        }
    }
}