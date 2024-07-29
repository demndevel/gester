package com.demn.findutil.di

import androidx.room.Room
import com.demn.data.AppDatabase
import com.demn.data.dao.PluginCacheDao
import com.demn.data.dao.ResultFrecencyDao
import com.demn.data.entities.PluginCommandCacheDbo
import com.demn.data.repo.PluginCacheRepositoryImpl
import com.demn.data.repo.ResultFrecencyRepositoryImpl
import com.demn.domain.data.PluginCacheRepository
import com.demn.domain.data.ResultFrecencyRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val dataModule = module {
    single<AppDatabase> {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            AppDatabaseName
        ).build()
    }

    single<ResultFrecencyDao> {
        val database = get<AppDatabase>()
        database.getResultUsagesDao()
    }

    single<PluginCacheDao> {
        val database = get<AppDatabase>()
        database.getPluginCommandCacheDao()
    }

    factory<ResultFrecencyRepository> { ResultFrecencyRepositoryImpl(get()) }
    factory<PluginCacheRepository> { PluginCacheRepositoryImpl(get()) }
}

const val AppDatabaseName = "com.demn.findutil.cache.db"