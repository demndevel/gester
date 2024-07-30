package com.demn.findutil.di

import com.demn.domain.plugin_management.OperationResultSorter
import com.demn.domain.plugin_management.PluginRepository
import com.demn.domain.plugin_management.PluginSettingsRepository
import com.demn.domain.plugin_management.PluginUninstaller
import com.demn.domain.plugin_providers.ExternalPluginsProvider
import com.demn.domain.settings.PluginAvailabilityRepository
import com.demn.findutil.app_settings.PluginAvailabilityRepositoryImpl
import com.demn.pluginloading.ExternalPluginsProviderImpl
import com.demn.pluginloading.MockOperationResultSorter
import com.demn.pluginloading.PluginRepositoryImpl
import com.demn.pluginloading.PluginSettingsRepositoryImpl
import com.demn.pluginloading.PluginUninstallerImpl
import org.koin.dsl.module

val pluginManagementModule = module {
    factory<PluginUninstaller> { PluginUninstallerImpl(get()) }
    factory<PluginSettingsRepository> { PluginSettingsRepositoryImpl(get(), get()) }
    factory<OperationResultSorter> { MockOperationResultSorter() }

    single<ExternalPluginsProvider> { ExternalPluginsProviderImpl(get(), get()) }
    single<PluginAvailabilityRepository> { PluginAvailabilityRepositoryImpl(get()) }
    single<PluginRepository> { PluginRepositoryImpl(get(), get()) }
}