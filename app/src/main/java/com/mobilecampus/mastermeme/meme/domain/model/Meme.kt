package com.mobilecampus.mastermeme.meme.domain.model

data class Meme(
    val id: Int,
    val title: String,
    val imageUri: String,  // Local file URI
    val isFavorite: Boolean,
    val createdAt: Long = System.currentTimeMillis()
)