package com.mobilecampus.mastermeme.meme.domain.model

data class Meme(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val isFavorite: Boolean
)