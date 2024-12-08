package com.mobilecampus.mastermeme

import android.app.Application
import com.mobilecampus.mastermeme.di.viewModelsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                viewModelsModule
            )
        }
    }
}