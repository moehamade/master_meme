package com.mobilecampus.mastermeme.meme.presentation.di

import com.mobilecampus.mastermeme.meme.presentation.meme_list.MemeListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::MemeListViewModel)
}