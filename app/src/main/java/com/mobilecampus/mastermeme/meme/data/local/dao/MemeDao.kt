package com.mobilecampus.mastermeme.meme.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mobilecampus.mastermeme.meme.data.local.entity.MemeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemeDao {
    @Query("SELECT * FROM memes ORDER BY createdAt DESC")
    fun getMemes(): Flow<List<MemeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeme(meme: MemeEntity)

    @Delete
    suspend fun deleteMeme(meme: MemeEntity)

    @Query("UPDATE memes SET isFavorite = NOT isFavorite WHERE id = :memeId")
    suspend fun toggleFavorite(memeId: Int)
}