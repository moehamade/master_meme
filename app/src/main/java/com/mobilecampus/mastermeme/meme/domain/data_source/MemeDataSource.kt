package com.mobilecampus.mastermeme.meme.domain.data_source

import android.graphics.Bitmap
import com.mobilecampus.mastermeme.meme.domain.model.MemeItem
import kotlinx.coroutines.flow.Flow

interface MemeDataSource {
    fun getMemes(): Flow<List<MemeItem.ImageMeme>>
    suspend fun saveMeme(meme: MemeItem.ImageMeme)
    suspend fun deleteMeme(meme: MemeItem.ImageMeme)
    suspend fun toggleFavorite(memeId: Int)
    suspend fun getTemplates(): List<String>
    suspend fun saveMemeImage(bitmap: Bitmap): String
}