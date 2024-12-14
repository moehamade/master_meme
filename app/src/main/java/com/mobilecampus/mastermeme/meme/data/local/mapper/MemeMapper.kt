package com.mobilecampus.mastermeme.meme.data.local.mapper

import com.mobilecampus.mastermeme.meme.data.local.entity.MemeEntity
import com.mobilecampus.mastermeme.meme.domain.model.MemeItem

fun MemeEntity.toDomain(): MemeItem.ImageMeme {
    return MemeItem.ImageMeme(
        id = id.toString(),  // Converting Int to String UUID for consistency
        imageUri = imageUri,
        description = title,  // Note that we're using 'title' as description
        isFavorite = isFavorite,
        createdAt = createdAt
    )
}

// Domain to Entity mappings
fun MemeItem.ImageMeme.toEntity(): MemeEntity {
    return MemeEntity(
        id = id.toIntOrNull() ?: 0,  // You might want to handle this conversion differently
        title = description ?: "",
        imageUri = imageUri,
        isFavorite = isFavorite,
        createdAt = createdAt
    )
}