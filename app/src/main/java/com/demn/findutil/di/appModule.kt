package com.demn.findutil.di

import com.demn.domain.settings.AppSettingsRepository
import com.demn.findutil.app_settings.AppSettingsRepositoryImpl
import com.demn.findutil.presentation.main.SearchScreenViewModel
import com.demn.findutil.presentation.settings.SettingsScreenViewModel
import com.demn.findutil.usecase.ProcessQueryUseCase
import com.demn.findutil.usecase.ProcessQueryUseCaseImpl
import org.koin.dsl.module

val appModule = module {
    factory<ProcessQueryUseCase> { ProcessQueryUseCaseImpl(get(), get(), get()) }
    factory<AppSettingsRepository> { AppSettingsRepositoryImpl(get()) }

    factory { SearchScreenViewModel(get(), get(), get()) }
    factory { SettingsScreenViewModel(get(), get(), get(), get(), get()) }
}