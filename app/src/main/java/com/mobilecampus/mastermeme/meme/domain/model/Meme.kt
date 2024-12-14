package com.mobilecampus.mastermeme.meme.domain.model

import kotlinx.serialization.Serializable

sealed interface MemeItem {
    val id: Int?
    val imageUri: String
    val description: String?

    @Serializable
    data class Template(
        override val id: Int? = null,
        override val imageUri: String, // This will be resource name like "meme_template_01"
        override val description: String?,
        val resourceId: Int  // Adding this to store the actual resource ID
    ) : MemeItem

    @Serializable
    data class ImageMeme(
        override val id: Int? = null,
        override val imageUri: String, // Local file URI
        override val description: String?,
        val isFavorite: Boolean = false,
        val createdAt: Long = System.currentTimeMillis()
    ) : MemeItem
}