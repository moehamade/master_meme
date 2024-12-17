package com.mobilecampus.mastermeme.meme.data.local.use_case

import com.mobilecampus.mastermeme.meme.domain.data_source.MemeDataSource
import com.mobilecampus.mastermeme.meme.domain.use_case.ShareMemesUseCase
import com.mobilecampus.mastermeme.meme.domain.util.FileManager

class ShareMemesUseCaseImpl(
    private val fileManager: FileManager,
    private val memeDataSource: MemeDataSource
) : ShareMemesUseCase {
    override suspend operator fun invoke(ids: Set<Int>) {
        val memes = memeDataSource.getMemesByIds(ids)
        val imageUris = memes.map { it.imageUri }.toSet()

        if (imageUris.isNotEmpty()) {
            fileManager.shareFiles(imageUris)
        }
    }
}