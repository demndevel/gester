package com.demn.findutil.di

import com.demn.domain.pluginmanagement.PluginRepository
import com.demn.domain.pluginmanagement.PluginSettingsRepository
import com.demn.domain.pluginmanagement.PluginUninstaller
import com.demn.domain.pluginproviders.ExternalPluginsProvider
import com.demn.domain.settings.PluginAvailabilityRepository
import com.demn.domain.usecase.OperationResultSorterUseCase
import com.demn.domain.usecase.OperationResultSorterUseCaseImpl
import com.demn.findutil.app_settings.PluginAvailabilityRepositoryImpl
import com.demn.pluginloading.ExternalPluginsProviderImpl
import com.demn.pluginloading.PluginRepositoryImpl
import com.demn.pluginloading.PluginSettingsRepositoryImpl
import com.demn.pluginloading.PluginUninstallerImpl
import org.koin.dsl.module

val pluginManagementModule = module {
    factory<PluginUninstaller> { PluginUninstallerImpl(get()) }
    factory<PluginSettingsRepository> { PluginSettingsRepositoryImpl(get(), get()) }
    factory<OperationResultSorterUseCase> { OperationResultSorterUseCaseImpl(get()) }

    single<ExternalPluginsProvider> { ExternalPluginsProviderImpl(get(), get()) }
    single<PluginAvailabilityRepository> { PluginAvailabilityRepositoryImpl(get()) }
    single<PluginRepository> { PluginRepositoryImpl(get(), get()) }
}