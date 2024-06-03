package com.demn.plugins

import com.demn.plugins.core_plugins.AppSearchingPlugin
import com.demn.plugins.core_plugins.CurrenciesPlugin
import org.koin.dsl.module

val corePluginsModule = module {
    factory<CorePluginsProvider> {
        CorePluginsProviderImpl(
            listOf(
                AppSearchingPlugin(get()),
                CurrenciesPlugin()
            )
        )
    }
}