package com.mobilecampus.mastermeme.meme.domain.model.editor

import androidx.compose.ui.geometry.Offset

data class TextBox(
    val id: Int,
    val text: String,
    val position: Offset = Offset.Zero,
    val style: MemeTextStyle = MemeTextStyle()
)