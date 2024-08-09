package com.demn.findutil.di.coreplugins

import androidx.room.Room
import com.demn.applications_core_plugin.ApplicationsRetriever
import com.demn.applications_core_plugin.database.ApplicationsDao
import com.demn.applications_core_plugin.database.ApplicationsDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

private const val applicationsDatabaseName = "applications_database"

val appSearchingPluginModule = module {
    single<ApplicationsDatabase> {
        Room.databaseBuilder(
            androidApplication(),
            ApplicationsDatabase::class.java,
            applicationsDatabaseName
        ).build()
    }

    single<ApplicationsDao> {
        val database = get<ApplicationsDatabase>()
        database.getApplicationsDao()
    }

    single<ApplicationsRetriever> {
        ApplicationsRetriever(get(), get())
    }
}