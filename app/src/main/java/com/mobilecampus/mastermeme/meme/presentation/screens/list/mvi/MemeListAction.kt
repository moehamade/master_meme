package com.mobilecampus.mastermeme.meme.presentation.screens.list.mvi

sealed interface MemeListAction {
    data object OnCreateMemeClick : MemeListAction
}