package com.demn.applications_core_plugin.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ApplicationsDao {
    @Query("SELECT * FROM applicationdbo")
    suspend fun getAll(): List<ApplicationDbo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(application: ApplicationDbo)
}