package com.mobilecampus.mastermeme.core.presentation.design_system

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.mobilecampus.mastermeme.R

object AppIcons {
    // icon via material icons library
    val add: ImageVector
        @Composable
        get() = Icons.Outlined.Add

    val undo: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.icon_undo)

    val redo: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.icon_redo)

    val meme: Array<Int> by lazy {
        Array(50) { index ->
            val number = (index + 1).toString().padStart(2, '0')
            val resourceName = "meme_template_$number"
            R.drawable::class.java.getField(resourceName).getInt(null)
        }
    }
}