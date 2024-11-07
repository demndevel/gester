package com.demn.data.dao

import androidx.room.*
import com.demn.data.entities.PluginCacheDbo
import com.demn.data.entities.PluginCommandCacheDbo
import com.demn.data.entities.PluginFallbackCommandCacheDbo
import com.demn.data.entities.PluginWithCommandsDbo
import java.util.UUID

@Dao
interface PluginCacheDao {
    @Transaction
    @Query("SELECT * FROM PluginCacheDbo")
    suspend fun getPluginsWithCommands(): List<PluginWithCommandsDbo>

    @Transaction
    @Query("SELECT * FROM PluginCacheDbo WHERE pluginId = :id")
    suspend fun getPluginWithCommands(id: String): PluginWithCommandsDbo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPluginCache(pluginCacheDbo: PluginCacheDbo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPluginCommands(commands: List<PluginCommandCacheDbo>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPluginFallbackCommands(commands: List<PluginFallbackCommandCacheDbo>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPluginCommand(command: PluginCommandCacheDbo)

    @Transaction
    suspend fun insertPluginWithCommands(pluginWithCommandsDbo: PluginWithCommandsDbo) {
        insertPluginCache(pluginWithCommandsDbo.pluginCacheDbo)
        insertPluginCommands(pluginWithCommandsDbo.commands)
        insertPluginFallbackCommands(pluginWithCommandsDbo.fallbackCommands)
    }

    @Query("DELETE FROM PluginCacheDbo WHERE pluginId = :pluginId")
    suspend fun deletePluginCache(pluginId: String)

    @Query("DELETE FROM PluginCommandCacheDbo WHERE pluginId = :pluginId")
    suspend fun deletePluginCommands(pluginId: String)

    @Query("DELETE FROM PluginFallbackCommandCacheDbo WHERE pluginId = :pluginId")
    suspend fun deletePluginFallbackCommands(pluginId: String)

    @Transaction
    suspend fun deletePluginWithCorrespondingData(pluginId: String) {
        deletePluginFallbackCommands(pluginId)
        deletePluginCommands(pluginId)
        deletePluginCache(pluginId)
    }
}