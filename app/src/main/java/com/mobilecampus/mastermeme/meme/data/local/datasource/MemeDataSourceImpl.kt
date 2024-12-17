package com.mobilecampus.mastermeme.meme.data.local.datasource

import android.content.Context
import android.graphics.Bitmap
import com.mobilecampus.mastermeme.core.presentation.design_system.AppIcons.meme
import com.mobilecampus.mastermeme.meme.data.local.dao.MemeDao
import com.mobilecampus.mastermeme.meme.data.local.mapper.toDomain
import com.mobilecampus.mastermeme.meme.data.local.mapper.toEntity
import com.mobilecampus.mastermeme.meme.domain.data_source.MemeDataSource
import com.mobilecampus.mastermeme.meme.domain.model.MemeItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File

class MemeLocalDataSourceImpl(
    private val context: Context,
    private val memeDao: MemeDao
) : MemeDataSource {

    private val memesDir by lazy {
        context.getDir("memes", Context.MODE_PRIVATE).also {
            if (!it.exists()) it.mkdirs()
        }
    }

    override fun getMemes(): Flow<List<MemeItem.ImageMeme>> {
        return memeDao.getMemes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getMemesByIds(ids: Set<Int>): List<MemeItem.ImageMeme> {
        return memeDao.getMemesByIds(ids).map { it.toDomain() }
    }

    override suspend fun saveMeme(meme: MemeItem.ImageMeme) {
        memeDao.insertMeme(meme.toEntity())
    }

    override suspend fun deleteMemes(ids: Set<Int>): Set<String> {
        // Get the full entities that need to be deleted
        val memesToDelete = memeDao.getMemesByIds(ids)

        // Get the URIs before deletion
        val imageUris = memesToDelete.map { it.imageUri }.toSet()

        // Delete the entities
        memeDao.deleteMemes(memesToDelete.toSet())

        // Return the URIs of deleted entries
        return imageUris
    }

    override suspend fun toggleFavorite(memeId: Int) {
        memeDao.toggleFavorite(memeId)
    }

    override suspend fun getTemplates(): List<String> {
        return withContext(Dispatchers.IO) {
            // Copy templates from assets to app's private directory if not already done
            context.assets.list("meme_templates")?.map { fileName ->
                val templateFile = File(memesDir, "template_$fileName")
                if (!templateFile.exists()) {
                    context.assets.open("meme_templates/$fileName").use { input ->
                        templateFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                }
                templateFile.absolutePath
            } ?: emptyList()
        }
    }

    override suspend fun saveMemeImage(bitmap: Bitmap): String {
        return withContext(Dispatchers.IO) {
            val fileName = "meme_${System.currentTimeMillis()}.jpg"
            val imageFile = File(memesDir, fileName)

            imageFile.outputStream().use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }

            imageFile.absolutePath
        }
    }
}