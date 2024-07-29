package com.demn.data.dao

import androidx.room.*
import com.demn.data.entities.ResultFrecencyDbo

@Dao
interface ResultFrecencyDao {
    @Query("SELECT * FROM ResultFrecencyDbo WHERE resultHashCode = :resultHashCode AND input = :input")
    suspend fun getResultFrecency(input: String, resultHashCode: Int): ResultFrecencyDbo?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertResultFrecency(resultFrecency: ResultFrecencyDbo)

    @Update
    suspend fun updateResultFrecency(resultFrecency: ResultFrecencyDbo)

    @Query("SELECT * FROM ResultFrecencyDbo WHERE input = :input")
    suspend fun getUsagesByInput(input: String): List<ResultFrecencyDbo>
}