package com.demn.gester.di

import com.demn.domain.pluginmanagement.PluginRepository
import com.demn.domain.pluginmanagement.PluginSettingsRepository
import com.demn.domain.pluginmanagement.PluginUninstaller
import com.demn.domain.pluginproviders.BoundServicePluginsProvider
import com.demn.domain.settings.PluginAvailabilityRepository
import com.demn.domain.usecase.OperationResultSorterUseCase
import com.demn.domain.usecase.OperationResultSorterUseCaseImpl
import com.demn.gester.app_settings.PluginAvailabilityRepositoryImpl
import com.demn.pluginloading.BoundServicePluginsProviderImpl
import com.demn.pluginloading.PluginRepositoryImpl
import com.demn.pluginloading.PluginSettingsRepositoryImpl
import com.demn.pluginloading.PluginUninstallerImpl
import org.koin.dsl.module

val pluginManagementModule = module {
    factory<PluginUninstaller> { PluginUninstallerImpl(get()) }
    factory<PluginSettingsRepository> { PluginSettingsRepositoryImpl(get(), get()) }
    factory<OperationResultSorterUseCase> { OperationResultSorterUseCaseImpl(get()) }

    single<BoundServicePluginsProvider> { BoundServicePluginsProviderImpl(get(), get()) }
    single<PluginAvailabilityRepository> { PluginAvailabilityRepositoryImpl(get()) }
    single<PluginRepository> { PluginRepositoryImpl(get()) }
}
