package com.mobilecampus.mastermeme.meme.domain.util

interface FileManager {
    suspend fun deleteFiles(uris: Set<String>)
}