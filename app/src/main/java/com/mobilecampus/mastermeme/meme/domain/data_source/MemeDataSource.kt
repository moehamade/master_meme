package com.mobilecampus.mastermeme.meme.domain.data_source

import android.graphics.Bitmap
import com.mobilecampus.mastermeme.meme.domain.model.Meme
import kotlinx.coroutines.flow.Flow

interface MemeDataSource {
    fun getMemes(): Flow<List<Meme>>
    suspend fun saveMeme(meme: Meme)
    suspend fun deleteMeme(meme: Meme)
    suspend fun toggleFavorite(memeId: Int)
    suspend fun getTemplates(): List<String>
    suspend fun saveMemeImage(bitmap: Bitmap): String
}