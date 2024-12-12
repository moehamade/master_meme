package com.mobilecampus.mastermeme.core.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    @Serializable data object MemeList: NavGraph
    @Serializable data class MemeEditor(val id: Int): NavGraph
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
            composable<NavGraph.MemeList> {
                MemeListScreenRoot({ resId ->
                    navController.navigate(NavGraph.MemeEditor(resId))
                })
            }

            composable<NavGraph.MemeEditor> {
                val args = it.toRoute<NavGraph.MemeEditor>()
               MemeEditorScreenRoot(backgroundImageResId = args.id)
            }
        }
    }
}