package com.demn.findutil.di.coreplugins

import com.demn.domain.plugin_providers.CorePluginsProvider
import com.demn.findutil.FindUtilPlugin
import com.demn.plugins.CorePluginsProviderImpl
import com.demn.plugins.CorePluginsSettingsRepository
import com.demn.plugins.CorePluginsSettingsRepositoryImpl
import com.demn.applications_core_plugin.AppSearchingPlugin
import com.demn.plugins.core_plugins.currencies.CurrenciesPlugin
import org.koin.dsl.module

val corePluginsModule = module {
    single<CorePluginsProvider> {
        CorePluginsProviderImpl(
            listOf(
                AppSearchingPlugin(get(), get()),
                CurrenciesPlugin(get()),
                FindUtilPlugin(get())
            ),
            get()
        )
    }

    factory<CorePluginsSettingsRepository> { CorePluginsSettingsRepositoryImpl(get()) }
}