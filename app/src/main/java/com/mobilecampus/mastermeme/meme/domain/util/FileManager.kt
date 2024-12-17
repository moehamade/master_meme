package com.mobilecampus.mastermeme.meme.domain.util

import android.content.Context
import android.net.Uri

interface FileManager {
    suspend fun deleteFiles(uris: Set<String>)
    suspend fun shareFiles(uris: Set<String>)
    fun getUriFromPath(path: String): Uri
}