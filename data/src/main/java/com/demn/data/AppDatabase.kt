package com.demn.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.demn.data.dao.PluginCacheDao
import com.demn.data.dao.ResultFrecencyDao
import com.demn.data.entities.PluginCacheDbo
import com.demn.data.entities.PluginCommandCacheDbo
import com.demn.data.entities.PluginFallbackCommandCacheDbo
import com.demn.data.entities.ResultFrecencyDbo

@Database(
    entities = [
        ResultFrecencyDbo::class,
        PluginCacheDbo::class,
        PluginCommandCacheDbo::class,
        PluginFallbackCommandCacheDbo::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getResultUsagesDao(): ResultFrecencyDao

    abstract fun getPluginCommandCacheDao(): PluginCacheDao
}