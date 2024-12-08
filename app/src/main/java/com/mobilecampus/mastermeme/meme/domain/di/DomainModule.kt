package com.mobilecampus.mastermeme.meme.domain.di

import com.mobilecampus.mastermeme.meme.data.local.use_case.DeleteMemeUseCaseImpl
import com.mobilecampus.mastermeme.meme.data.local.use_case.GetMemesUseCaseImpl
import com.mobilecampus.mastermeme.meme.data.local.use_case.GetTemplatesUseCaseImpl
import com.mobilecampus.mastermeme.meme.data.local.use_case.ToggleFavoriteUseCaseImpl
import com.mobilecampus.mastermeme.meme.domain.use_case.DeleteMemeUseCase
import com.mobilecampus.mastermeme.meme.domain.use_case.GetMemesUseCase
import com.mobilecampus.mastermeme.meme.domain.use_case.GetTemplatesUseCase
import com.mobilecampus.mastermeme.meme.domain.use_case.ToggleFavoriteUseCase
import org.koin.dsl.module

val domainModule = module {
    factory<GetMemesUseCase> { GetMemesUseCaseImpl(get()) }
    factory<ToggleFavoriteUseCase> { ToggleFavoriteUseCaseImpl(get()) }
    factory<DeleteMemeUseCase> { DeleteMemeUseCaseImpl(get()) }
    factory<GetTemplatesUseCase> { GetTemplatesUseCaseImpl(get()) }
}