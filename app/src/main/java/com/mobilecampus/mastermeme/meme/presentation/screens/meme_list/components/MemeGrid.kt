package com.mobilecampus.mastermeme.meme.presentation.screens.meme_list.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.mobilecampus.mastermeme.meme.domain.model.MemeItem


// Extension function for template grids
fun LazyGridScope.templateItems(
    templates: List<MemeItem.Template>,
    onTemplateClick: (MemeItem.Template) -> Unit,
    itemSpacing: Dp = 8.dp
) {
    items(
        items = templates,
        // Using resourceId as key since it's guaranteed to be non-null
        key = { it.resourceId }
    ) { template ->
        TemplateCard(
            template = template,
            onClick = onTemplateClick,
            modifier = Modifier.padding(itemSpacing)
        )
    }
}

// Extension function for user-created meme grids
fun LazyGridScope.userMemeItems(
    memes: List<MemeItem.ImageMeme>,
    onMemeClick: (MemeItem.ImageMeme) -> Unit,
    modifier: Modifier = Modifier,
    onFavoriteToggle: (MemeItem.ImageMeme) -> Unit,
    isSelectionMode: Boolean = false,
    selectedMemes: Set<Int> = emptySet(), // Changed from Set<String> to Set<Int>
    onSelectionToggle: (MemeItem.ImageMeme, Boolean) -> Unit,
    itemSpacing: Dp = 8.dp
) {
    items(
        items = memes,
        // Using createdAt as key since id might be null
        key = { it.createdAt }
    ) { meme ->
        ImageMemeCard(
            meme = meme,
            onClick = onMemeClick,
            modifier = modifier.padding(itemSpacing),
            onFavoriteToggle = onFavoriteToggle,
            isSelectionMode = isSelectionMode,
            // Only check selection if id is not null
            isSelected = meme.id?.let { selectedMemes.contains(it) } ?: false,
            onSelectionToggle = onSelectionToggle
        )
    }
}

@Composable
fun TemplateGrid(
    templates: List<MemeItem.Template>,
    onTemplateClick: (MemeItem.Template) -> Unit,
    modifier: Modifier = Modifier,
    columns: Int = 2,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    itemSpacing: Dp = 8.dp
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(itemSpacing),
        verticalArrangement = Arrangement.spacedBy(itemSpacing)
    ) {
        templateItems(
            templates = templates,
            onTemplateClick = onTemplateClick,
            itemSpacing = itemSpacing
        )
    }
}

@Composable
fun UserMemeGrid(
    memes: List<MemeItem.ImageMeme>,
    onMemeTap: (MemeItem.ImageMeme) -> Unit,
    onFavoriteToggle: (MemeItem.ImageMeme) -> Unit,
    modifier: Modifier = Modifier,
    columns: Int = 2,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    itemSpacing: Dp = 8.dp,
    isSelectionMode: Boolean = false,
    selectedMemes: Set<Int> = emptySet(), // Changed from Set<String> to Set<Int>
    onSelectionToggle: (MemeItem.ImageMeme, Boolean) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(itemSpacing),
        verticalArrangement = Arrangement.spacedBy(itemSpacing)
    ) {
        userMemeItems(
            memes = memes,
            onMemeClick = onMemeTap,
            onFavoriteToggle = onFavoriteToggle,
            isSelectionMode = isSelectionMode,
            selectedMemes = selectedMemes,
            onSelectionToggle = onSelectionToggle,
            itemSpacing = itemSpacing
        )
    }
}


// Base card component that handles common layout and behavior
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MemeCardBase(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)  // Square aspect ratio for consistent layout
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

// Specialized component for template memes
@Composable
fun TemplateCard(
    template: MemeItem.Template,
    onClick: (MemeItem.Template) -> Unit,
    modifier: Modifier = Modifier
) {

    MemeCardBase(
        onClick = { onClick(template) },
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = template.resourceId),
            contentDescription = template.description,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

// Specialized component for user-created memes
@Composable
fun ImageMemeCard(
    meme: MemeItem.ImageMeme,
    onClick: (MemeItem.ImageMeme) -> Unit,
    modifier: Modifier = Modifier,
    onFavoriteToggle: ((MemeItem.ImageMeme) -> Unit)? = null,
    isSelectionMode: Boolean = false,
    isSelected: Boolean = false,
    onSelectionToggle: ((MemeItem.ImageMeme, Boolean) -> Unit)? = null,
) {
    MemeCardBase(
        onClick = {
            if (isSelectionMode) {
                onSelectionToggle?.invoke(meme, !isSelected)
            } else {
                onClick(meme)
            }
        },
        onLongClick = {
            if (!isSelectionMode) {
                onSelectionToggle?.invoke(meme, true)
            }
        },
        modifier = modifier
    ) {
        // Main meme image
        AsyncImage(
            model = meme.imageUri,
            contentDescription = meme.description,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Selection checkbox overlay
        if (isSelectionMode) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { checked ->
                    onSelectionToggle?.invoke(meme, checked)
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            )
        }

        // Favorite button (only shown when not in selection mode)
        if (!isSelectionMode && onFavoriteToggle != null) {
            IconButton(
                onClick = { onFavoriteToggle(meme) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = if (meme.isFavorite) {
                        Icons.Filled.Favorite
                    } else {
                        Icons.Outlined.FavoriteBorder
                    },
                    contentDescription = if (meme.isFavorite) {
                        "Remove from favorites"
                    } else {
                        "Add to favorites"
                    },
                    tint = if (meme.isFavorite) Color.Red else Color.White
                )
            }
        }
    }
}