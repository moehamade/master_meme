package com.mobilecampus.mastermeme.meme.di

import com.mobilecampus.mastermeme.App
import com.mobilecampus.mastermeme.meme.data.local.use_case.DeleteMemeUseCaseImpl
import com.mobilecampus.mastermeme.meme.data.local.use_case.GetMemesUseCaseImpl
import com.mobilecampus.mastermeme.meme.data.local.use_case.GetTemplatesUseCaseImpl
import com.mobilecampus.mastermeme.meme.data.local.use_case.SaveMemeUseCaseImpl
import com.mobilecampus.mastermeme.meme.data.local.use_case.ShareMemesUseCaseImpl
import com.mobilecampus.mastermeme.meme.data.local.use_case.ShareTemporaryMemeImpl
import com.mobilecampus.mastermeme.meme.data.local.use_case.ToggleFavoriteUseCaseImpl
import com.mobilecampus.mastermeme.meme.presentation.screens.editor.util.MemeRenderer
import com.mobilecampus.mastermeme.meme.domain.use_case.DeleteMemeUseCase
import com.mobilecampus.mastermeme.meme.domain.use_case.GetMemesUseCase
import com.mobilecampus.mastermeme.meme.domain.use_case.GetTemplatesUseCase
import com.mobilecampus.mastermeme.meme.domain.use_case.SaveMemeUseCase
import com.mobilecampus.mastermeme.meme.domain.use_case.ShareMemesUseCase
import com.mobilecampus.mastermeme.meme.domain.use_case.ShareTemporaryMeme
import com.mobilecampus.mastermeme.meme.domain.use_case.ToggleFavoriteUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::GetMemesUseCaseImpl).bind<GetMemesUseCase>()
    factoryOf(::ToggleFavoriteUseCaseImpl).bind<ToggleFavoriteUseCase>()
    factory<DeleteMemeUseCase> { DeleteMemeUseCaseImpl(get(), get(), applicationScope = App.applicationScope ) }
    factoryOf(::GetTemplatesUseCaseImpl).bind<GetTemplatesUseCase>()
    factoryOf(::SaveMemeUseCaseImpl).bind<SaveMemeUseCase>()


    singleOf(::ShareTemporaryMemeImpl).bind<ShareTemporaryMeme>()
    singleOf(::ShareMemesUseCaseImpl).bind<ShareMemesUseCase>()
    singleOf(::MemeRenderer).bind()
}