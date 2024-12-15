package com.mobilecampus.mastermeme.meme.data.local.util

import android.util.Log
import com.mobilecampus.mastermeme.meme.domain.util.FileManager
import java.io.File

class FileManagerImpl : FileManager {
    override suspend fun deleteFiles(uris: Set<String>) {
        uris.forEach { uri ->
            try {
                val file = File(uri)
                if (file.exists() && !file.delete()) {
                    Log.e("FileManager", "Failed to delete file: $uri")
                }
            } catch (e: Exception) {
                Log.e("FileManager", "Error deleting file: $uri", e)
            }
        }
    }
}