package com.mobilecampus.mastermeme

import android.app.Application
import com.mobilecampus.mastermeme.meme.data.di.memeDataModule
import com.mobilecampus.mastermeme.meme.domain.di.domainModule
import com.mobilecampus.mastermeme.meme.presentation.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@App)
            modules(listOf(memeDataModule, domainModule, presentationModule))
        }
    }
}