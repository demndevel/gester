package com.demn.findutil

import android.app.Application
import com.demn.findutil.di.appModule
import com.demn.findutil.di.coreplugins.appSearchingPluginModule
import com.demn.findutil.di.dataModule
import com.demn.findutil.di.domainModule
import com.demn.findutil.di.pluginManagementModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

class GesterApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (GlobalContext.getKoinApplicationOrNull() == null) {
            startKoin {
                androidContext(applicationContext)
                modules(
                    listOf(
                        appModule,
                        appSearchingPluginModule,
                        dataModule,
                        pluginManagementModule,
                        domainModule
                    )
                )
            }
        }
    }
}