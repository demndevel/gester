package com.demn.findutil.di.coreplugins

import com.demn.domain.pluginproviders.CorePluginsProvider
import com.demn.findutil.FindUtilPlugin
import com.demn.coreplugins.base.CorePluginsProviderImpl
import com.demn.coreplugins.base.CorePluginsSettingsRepository
import com.demn.coreplugins.base.CorePluginsSettingsRepositoryImpl
import com.demn.applications_core_plugin.AppSearchingPlugin
import com.demn.unit_calc_core_plugin.UnitCalcCorePlugin
import org.koin.dsl.module

val corePluginsModule = module {
    single<CorePluginsProvider> {
        CorePluginsProviderImpl(
            listOf(
                AppSearchingPlugin(get(), get()),
                FindUtilPlugin(get()),
                UnitCalcCorePlugin()
            ),
            get()
        )
    }

    factory<CorePluginsSettingsRepository> { CorePluginsSettingsRepositoryImpl(get()) }
}