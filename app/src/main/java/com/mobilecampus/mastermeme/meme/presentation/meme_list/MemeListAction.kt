package com.mobilecampus.mastermeme.meme.presentation.meme_list

sealed interface MemeListAction {
    data object OnCreateMemeClick : MemeListAction
}