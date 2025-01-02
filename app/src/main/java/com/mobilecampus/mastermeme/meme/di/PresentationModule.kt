package com.mobilecampus.mastermeme.meme.di

import com.mobilecampus.mastermeme.meme.presentation.screens.editor.util.MemeRenderer
import com.mobilecampus.mastermeme.meme.presentation.screens.editor.MemeEditorViewModel
import com.mobilecampus.mastermeme.meme.presentation.screens.meme_list.MemeListViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::MemeListViewModel)
    viewModelOf(::MemeEditorViewModel)
    singleOf(::MemeRenderer)
}