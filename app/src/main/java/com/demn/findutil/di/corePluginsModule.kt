package com.demn.findutil.di

import com.demn.plugins.core_plugins.AppSearchingPlugin
import com.demn.plugins.core_plugins.CurrenciesPlugin
import com.demn.findutil.FindUtilPlugin
import com.demn.plugins.CorePluginsProvider
import com.demn.plugins.CorePluginsProviderImpl
import org.koin.dsl.module

val corePluginsModule = module {
    factory<CorePluginsProvider> {
        CorePluginsProviderImpl(
            listOf(
                AppSearchingPlugin(get()),
                CurrenciesPlugin(),
                FindUtilPlugin(get())
            )
        )
    }
}