package com.mobilecampus.mastermeme.meme.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

sealed interface MemeItem {
    val id: String
    val imageUri: String
    val description: String?

    @Serializable
    data class Template(
        override val id: String,
        override val imageUri: String, // This will be resource name like "meme_template_01"
        override val description: String?,
        val resourceId: Int  // Adding this to store the actual resource ID
    ) : MemeItem

    @Serializable
    data class ImageMeme(
        override val id: String = UUID.randomUUID().toString(),
        override val imageUri: String, // Local file URI
        override val description: String?,
        val isFavorite: Boolean = false,
        val createdAt: Long = System.currentTimeMillis()
    ) : MemeItem
}