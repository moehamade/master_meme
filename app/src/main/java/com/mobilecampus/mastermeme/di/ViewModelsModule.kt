package com.mobilecampus.mastermeme.di

import com.mobilecampus.mastermeme.meme.presentation.screens.list.MemeListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

var viewModelsModule = module {
    viewModelOf(::MemeListViewModel)
}