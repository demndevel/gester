package com.demn.findutil.di

import com.demn.findutil.app_settings.AppSettingsRepository
import com.demn.findutil.app_settings.AppSettingsRepositoryImpl
import com.demn.findutil.presentation.main.SearchScreenViewModel
import com.demn.findutil.presentation.settings.SettingsScreenViewModel
import com.demn.findutil.usecase.ProcessQueryUseCase
import com.demn.findutil.usecase.ProcessQueryUseCaseImpl
import com.demn.pluginloading.*
import org.koin.dsl.module

val appModule = module {
    factory<ProcessQueryUseCase> { ProcessQueryUseCaseImpl(get()) }
    single<PluginRepository> { PluginRepositoryImpl(get(), get()) }
    factory<ExternalPluginsProvider> { ExternalPluginsProviderImpl(get()) }
    factory<PluginSettingsRepository> { PluginSettingsRepositoryImpl(get(), get()) }
    factory<AppSettingsRepository> { AppSettingsRepositoryImpl(get()) }

    factory { SearchScreenViewModel(get(), get()) }
    factory { SettingsScreenViewModel(get(), get(), get()) }
}