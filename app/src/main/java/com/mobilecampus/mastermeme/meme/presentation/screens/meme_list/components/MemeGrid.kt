package com.mobilecampus.mastermeme.meme.presentation.screens.meme_list.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.mobilecampus.mastermeme.core.presentation.design_system.RoundedCheckbox
import com.mobilecampus.mastermeme.meme.domain.model.MemeItem
import com.mobilecampus.mastermeme.meme.domain.model.SortOption
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


object AnimationSpecs {
    val fadeIn = tween<Float>(
        durationMillis = 300,
        easing = FastOutSlowInEasing
    )
    val fadeOut = tween<Float>(
        durationMillis = 200,
        easing = FastOutLinearInEasing
    )
    val slideIn = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
    val scaleIn = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
}

fun Modifier.gridItemAnimation(
    visible: Boolean = true,
    index: Int
) = composed {
    val offsetY = remember { Animatable(if (visible) 0f else 100f) }
    val alpha = remember { Animatable(if (visible) 1f else 0f) }
    val scale = remember { Animatable(if (visible) 1f else 0.8f) }

    LaunchedEffect(visible) {
        launch {
            if (visible) {
                delay(index * 50L) // Staggered animation
                offsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = AnimationSpecs.slideIn
                )
            } else {
                offsetY.animateTo(
                    targetValue = 100f,
                    animationSpec = AnimationSpecs.slideIn
                )
            }
        }
        launch {
            if (visible) {
                delay(index * 50L)
                alpha.animateTo(
                    targetValue = 1f,
                    animationSpec = AnimationSpecs.fadeIn
                )
            } else {
                alpha.animateTo(
                    targetValue = 0f,
                    animationSpec = AnimationSpecs.fadeOut
                )
            }
        }
        launch {
            if (visible) {
                delay(index * 50L)
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = AnimationSpecs.scaleIn
                )
            } else {
                scale.animateTo(
                    targetValue = 0.8f,
                    animationSpec = AnimationSpecs.scaleIn
                )
            }
        }
    }

    this
        .graphicsLayer {
            translationY = offsetY.value
            this.alpha = alpha.value
            scaleX = scale.value
            scaleY = scale.value
        }
}

fun LazyGridScope.userMemes(
    memes: List<MemeItem.ImageMeme>,
    onMemeTap: (MemeItem.ImageMeme) -> Unit,
    onFavoriteToggle: (MemeItem.ImageMeme) -> Unit,
    itemSpacing: Dp,
    isSelectionMode: Boolean = false,
    selectedMemes: Set<Int> = emptySet(),
    onSelectionToggle: (MemeItem.ImageMeme, Boolean) -> Unit
) {
    itemsIndexed(
        items = memes,
        key = { _, meme -> meme.id!! }
    ) { index, meme ->
        Box(
            modifier = Modifier
                .gridItemAnimation(index = index)
                .animatedScaleOnLoad(
                    resourceId = meme.id!!,
                    durationMillis = 300
                )
                .animateItem(
                    fadeInSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessMediumLow,
                        visibilityThreshold = IntOffset.VisibilityThreshold
                    ),
                    fadeOutSpec = spring(stiffness = Spring.StiffnessMediumLow)
                ),
        ) {
            ImageMemeCard(
                meme = meme,
                onClick = onMemeTap,
                onFavoriteToggle = onFavoriteToggle,
                isSelectionMode = isSelectionMode,
                isSelected = meme.id.let { selectedMemes.contains(it) },
                onSelectionToggle = onSelectionToggle,
                modifier = Modifier.padding(itemSpacing)
            )
        }
    }
}

fun LazyGridScope.templates(
    templates: List<MemeItem.Template>,
    onTemplateClick: (MemeItem.Template) -> Unit,
    visibleState: MutableTransitionState<Boolean>,
    itemSpacing: Dp
) {
    itemsIndexed(
        items = templates,
        key = { _, template -> template.resourceId }
    ) { index, template ->
        Box(
            modifier = Modifier
                .animatedScaleOnLoad(
                    resourceId = template.resourceId,
                    durationMillis = 300
                )
                .gridItemAnimation(
                    visible = visibleState.targetState,
                    index = index
                )
        ) {
            TemplateCard(
                template = template,
                onClick = onTemplateClick,
                modifier = Modifier.padding(itemSpacing)
            )
        }
    }
}

@Composable
fun AnimatedTemplateGrid(
    templates: List<MemeItem.Template>,
    onTemplateClick: (MemeItem.Template) -> Unit,
    modifier: Modifier = Modifier,
    columns: Int = 2,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    itemSpacing: Dp = 8.dp
) {
    val visibleState = remember { MutableTransitionState(false) }

    LaunchedEffect(Unit) {
        visibleState.targetState = true
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(itemSpacing),
        verticalArrangement = Arrangement.spacedBy(itemSpacing)
    ) {
        templates(
            templates = templates,
            onTemplateClick = onTemplateClick,
            visibleState = visibleState,
            itemSpacing = itemSpacing
        )
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun UserMemeGrid(
    memes: List<MemeItem.ImageMeme>,
    state: LazyGridState,
    onMemeTap: (MemeItem.ImageMeme) -> Unit,
    onFavoriteToggle: (MemeItem.ImageMeme) -> Unit,
    modifier: Modifier = Modifier,
    columns: Int = 2,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    itemSpacing: Dp = 8.dp,
    isSelectionMode: Boolean = false,
    selectedMemes: Set<Int> = emptySet(),
    onSelectionToggle: (MemeItem.ImageMeme, Boolean) -> Unit,
    sortOption: SortOption
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(sortOption) {
        scope.launch {
            state.animateScrollToItem(0)
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier,
        state = state,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(itemSpacing),
        verticalArrangement = Arrangement.spacedBy(itemSpacing)
    ) {
        userMemes(
            memes = memes,
            onMemeTap = onMemeTap,
            onFavoriteToggle = { meme ->
                scope.launch {
                    val currentIndex = memes.indexOf(meme)
                    val isFirstVisibleItem = state.firstVisibleItemIndex == currentIndex
                    val currentOffset = state.firstVisibleItemScrollOffset

                    if (isFirstVisibleItem && meme.isFavorite && sortOption == SortOption.FAVORITES_FIRST) {
                        val nextVisibleItemIndex = (state.firstVisibleItemIndex + columns).coerceAtMost(memes.size - 1)
                        onFavoriteToggle(meme)
                        state.scrollToItem(nextVisibleItemIndex, currentOffset)
                    } else {
                        onFavoriteToggle(meme)
                    }
                }
            },
            itemSpacing = itemSpacing,
            isSelectionMode = isSelectionMode,
            selectedMemes = selectedMemes,
            onSelectionToggle = onSelectionToggle
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

fun Modifier.animatedScaleOnLoad(
    resourceId: Int,
    initialScale: Float = 0.8f,
    targetScale: Float = 1f,
    durationMillis: Int = 300
): Modifier = composed {
    val scale = remember(resourceId) { Animatable(initialScale) }

    LaunchedEffect(resourceId) {
        scale.snapTo(initialScale)
        scale.animateTo(
            targetValue = targetScale,
            animationSpec = tween(durationMillis)
        )
    }

    this.scale(scale.value)
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
            RoundedCheckbox(
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
                    tint = MaterialTheme.colorScheme.primaryContainer
                )
            }
        }
    }
}