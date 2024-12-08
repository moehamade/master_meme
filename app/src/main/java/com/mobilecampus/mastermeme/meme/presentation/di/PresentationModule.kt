package com.mobilecampus.mastermeme.meme.presentation.di

import com.mobilecampus.mastermeme.meme.presentation.meme_list.MemeListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel {
        MemeListViewModel(
            getMemesUseCase = get(),
            toggleFavoriteUseCase = get(),
            deleteMemesUseCase = get(),
            getTemplatesUseCase = get()
        )
    }
}