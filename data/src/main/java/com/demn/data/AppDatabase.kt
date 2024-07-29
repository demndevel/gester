package com.demn.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.demn.data.dao.ResultFrecencyDao
import com.demn.data.entities.ResultFrecencyDbo

@Database(entities = [ResultFrecencyDbo::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getResultUsagesDao(): ResultFrecencyDao
}