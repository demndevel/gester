package com.demn.gester

import android.app.Application
import com.demn.gester.di.appModule
import com.demn.gester.di.coreplugins.appSearchingPluginModule
import com.demn.gester.di.dataModule
import com.demn.gester.di.domainModule
import com.demn.gester.di.pluginManagementModule
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
