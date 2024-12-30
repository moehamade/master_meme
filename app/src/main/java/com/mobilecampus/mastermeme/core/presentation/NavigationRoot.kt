package com.mobilecampus.mastermeme.core.presentation

import androidx.annotation.DrawableRes
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.mobilecampus.mastermeme.core.presentation.design_system.MasterMemeBackground
import com.mobilecampus.mastermeme.meme.presentation.screens.editor.MemeEditorScreenRoot
import com.mobilecampus.mastermeme.meme.presentation.screens.meme_list.MemeListScreenRoot
import kotlinx.serialization.Serializable

sealed interface NavGraph {
    @Serializable
    data object MemeList : NavGraph

    @Serializable
    data class MemeEditor(@DrawableRes val resId: Int) : NavGraph
}

@Composable
fun NavigationRoot(
    innerPadding: PaddingValues,
    navController: NavHostController,
) {
    MasterMemeBackground {
        NavHost(
            navController = navController,
            startDestination = NavGraph.MemeList,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    PaddingValues(
                        bottom = innerPadding.calculateBottomPadding(),
                        start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                        end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
                    )
                )
        ) {
            composable<NavGraph.MemeList>(
                enterTransition = {
                    fadeIn(
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    ) + scaleIn(
                        initialScale = 1.1f,
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    )
                },
                exitTransition = {
                    fadeOut(
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    ) + scaleOut(
                        targetScale = 0.9f,
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    )
                }
            ) {
                MemeListScreenRoot { resId ->
                    navController.navigate(NavGraph.MemeEditor(resId))
                }
            }

            composable<NavGraph.MemeEditor>(
                enterTransition = {
                    fadeIn(
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    ) + scaleIn(
                        initialScale = 0.9f,
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    )
                },
                exitTransition = {
                    fadeOut(
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    ) + scaleOut(
                        targetScale = 1.1f,
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    )
                },
                sizeTransform = {
                    SizeTransform(
                        sizeAnimationSpec = { _, _ ->
                            spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMediumLow,
                                visibilityThreshold = IntSize.VisibilityThreshold
                            )
                        },
                        clip = false
                    )
                }
            ) {
                val args = it.toRoute<NavGraph.MemeEditor>()
                MemeEditorScreenRoot(
                    backgroundImageResId = args.resId,
                    onNavigateBack = {
                        navController.navigateUp()
                    }
                )
            }
        }
    }
}