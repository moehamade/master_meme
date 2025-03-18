package com.mobilecampus.mastermeme.meme.data.local.use_case

import com.mobilecampus.mastermeme.meme.domain.data_source.MemeDataSource
import com.mobilecampus.mastermeme.meme.domain.use_case.DeleteMemeUseCase
import com.mobilecampus.mastermeme.meme.domain.util.FileManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeleteMemeUseCaseImpl(
    private val dataSource: MemeDataSource,
    private val fileManager: FileManager,
    private val applicationScope: CoroutineScope
) : DeleteMemeUseCase {
    override suspend operator fun invoke(ids: Set<Int>) {
        withContext(Dispatchers.IO) {
            try {
                val deletedImageUris = dataSource.deleteMemes(ids)
                applicationScope.launch(Dispatchers.IO) {
                    fileManager.deleteFiles(deletedImageUris)
                }
            } catch (e: Exception) {
                throw Exception("Failed to delete memes: ${e.message}")
            }
        }
    }
}