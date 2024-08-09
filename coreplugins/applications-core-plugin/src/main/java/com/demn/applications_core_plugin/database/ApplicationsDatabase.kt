package com.demn.applications_core_plugin.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ApplicationDbo::class],
    version = 1,
    exportSchema = false
)
abstract class ApplicationsDatabase : RoomDatabase() {
    abstract fun getApplicationsDao(): ApplicationsDao
}