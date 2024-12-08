package com.mobilecampus.mastermeme.meme.data.local.mapper

import com.mobilecampus.mastermeme.meme.data.local.entity.MemeEntity
import com.mobilecampus.mastermeme.meme.domain.model.Meme

// Extension function to convert MemeEntity to domain Meme
fun MemeEntity.toDomain(): Meme {
    return Meme(
        id = id,
        title = title,
        imageUri = imageUri,
        isFavorite = isFavorite,
        createdAt = createdAt
    )
}

// Extension function to convert domain Meme to MemeEntity
fun Meme.toEntity(): MemeEntity {
    return MemeEntity(
        id = id,
        title = title,
        imageUri = imageUri,
        isFavorite = isFavorite,
        createdAt = createdAt
    )
}