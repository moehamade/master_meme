package com.mobilecampus.mastermeme.meme.data.local.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.mobilecampus.mastermeme.meme.domain.util.FileManager
import java.io.File

class FileManagerImpl(
    private val context: Context
) : FileManager {
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
    override suspend fun shareFiles(uris: Set<String>) {
        try {
            val shareUris = uris.map { getUriFromPath(it) }

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND_MULTIPLE
                type = "image/*"
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(shareUris))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooserIntent = Intent.createChooser(shareIntent, "Share Memes")
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooserIntent)
        } catch (e: Exception) {
            Log.e("FileManager", "Error sharing files", e)
            throw e
        }
    }

    override fun getUriFromPath(path: String): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            File(path)
        )
    }
}