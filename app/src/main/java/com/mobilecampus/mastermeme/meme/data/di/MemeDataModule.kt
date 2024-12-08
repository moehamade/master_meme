package com.mobilecampus.mastermeme.meme.data.di

import androidx.room.Room
import com.mobilecampus.mastermeme.meme.data.local.database.MemeDatabase
import com.mobilecampus.mastermeme.meme.data.local.datasource.MemeLocalDataSourceImpl
import com.mobilecampus.mastermeme.meme.domain.data_source.MemeDataSource
import org.koin.dsl.module

val memeDataModule = module {
    single { Room.databaseBuilder(get(), MemeDatabase::class.java, "meme_database").build() }
    single { get<MemeDatabase>().memeDao() }
    single<MemeDataSource> { MemeLocalDataSourceImpl(get(), get()) }
}