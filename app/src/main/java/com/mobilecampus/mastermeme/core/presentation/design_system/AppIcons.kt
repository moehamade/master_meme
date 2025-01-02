package com.mobilecampus.mastermeme.core.presentation.design_system

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
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

    val arrowBack: ImageVector
        @Composable
        get() = Icons.AutoMirrored.Filled.ArrowBack

    val save: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.ic_save)

    val share: ImageVector
        @Composable
        get() = Icons.Default.Share

    val meme = listOf(
        R.drawable.meme_template_01,
        R.drawable.meme_template_02,
        R.drawable.meme_template_03,
        R.drawable.meme_template_04,
        R.drawable.meme_template_05,
        R.drawable.meme_template_06,
        R.drawable.meme_template_07,
        R.drawable.meme_template_08,
        R.drawable.meme_template_09,
        R.drawable.meme_template_10,
        R.drawable.meme_template_11,
        R.drawable.meme_template_12,
        R.drawable.meme_template_13,
        R.drawable.meme_template_14,
        R.drawable.meme_template_15,
        R.drawable.meme_template_16,
        R.drawable.meme_template_17,
        R.drawable.meme_template_18,
        R.drawable.meme_template_19,
        R.drawable.meme_template_20,
        R.drawable.meme_template_21,
        R.drawable.meme_template_22,
        R.drawable.meme_template_23,
        R.drawable.meme_template_24,
        R.drawable.meme_template_25,
        R.drawable.meme_template_26,
        R.drawable.meme_template_27,
        R.drawable.meme_template_28,
        R.drawable.meme_template_29,
        R.drawable.meme_template_30,
        R.drawable.meme_template_31,
        R.drawable.meme_template_32,
        R.drawable.meme_template_33,
        R.drawable.meme_template_34,
        R.drawable.meme_template_35,
        R.drawable.meme_template_36,
        R.drawable.meme_template_37,
        R.drawable.meme_template_38,
        R.drawable.meme_template_39,
        R.drawable.meme_template_40,
        R.drawable.meme_template_41,
        R.drawable.meme_template_42,
        R.drawable.meme_template_43,
        R.drawable.meme_template_44,
        R.drawable.meme_template_45,
        R.drawable.meme_template_46,
        R.drawable.meme_template_47,
        R.drawable.meme_template_48,
        R.drawable.meme_template_49,
        R.drawable.meme_template_50,
        R.drawable.meme_template_51,
        R.drawable.meme_template_52
    )
}