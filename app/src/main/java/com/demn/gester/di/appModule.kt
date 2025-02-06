package com.demn.gester.di

import com.demn.domain.settings.AppSettingsRepository
import com.demn.gester.app_settings.AppSettingsRepositoryImpl
import com.demn.gester.presentation.main.SearchScreenViewModel
import com.demn.gester.presentation.settings.SettingsScreenViewModel
import org.koin.dsl.module

val appModule = module {
    factory<AppSettingsRepository> { AppSettingsRepositoryImpl(get()) }

    factory { SearchScreenViewModel(get(), get(), get()) }
    factory { SettingsScreenViewModel(get(), get(), get(), get(), get(), get()) }
}
