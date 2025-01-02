package com.mobilecampus.mastermeme.meme.di

import androidx.room.Room
import com.mobilecampus.mastermeme.meme.data.local.database.MemeDatabase
import com.mobilecampus.mastermeme.meme.data.local.datasource.MemeLocalDataSourceImpl
import com.mobilecampus.mastermeme.meme.data.local.util.FileManagerImpl
import com.mobilecampus.mastermeme.meme.domain.data_source.MemeDataSource
import com.mobilecampus.mastermeme.meme.domain.util.FileManager
import eu.anifantakis.lib.securepersist.PersistManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    single { Room.databaseBuilder(get(), MemeDatabase::class.java, "meme_database").build() }
    single { get<MemeDatabase>().memeDao() }
    singleOf(::MemeLocalDataSourceImpl).bind<MemeDataSource>()
    singleOf(::FileManagerImpl).bind<FileManager>()
    single<PersistManager> {
        PersistManager(androidContext())
    }
}