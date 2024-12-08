package com.mobilecampus.mastermeme.meme.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mobilecampus.mastermeme.meme.data.local.dao.MemeDao
import com.mobilecampus.mastermeme.meme.data.local.entity.MemeEntity

@Database(entities = [MemeEntity::class], version = 1, exportSchema = false)
abstract class MemeDatabase : RoomDatabase() {
    abstract fun memeDao(): MemeDao
}