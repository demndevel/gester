package com.demn.appsearchplugin.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ApplicationDbo::class],
    version = 1,
    exportSchema = false
)
abstract class ApplicationsDatabase : RoomDatabase() {
    abstract fun getApplicationsDao(): ApplicationsDao

    companion object {

        private var INSTANCE: ApplicationsDatabase? = null

        fun getInstance(context: Context): ApplicationsDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context, ApplicationsDatabase::class.java, "yourdb.db").build()
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}