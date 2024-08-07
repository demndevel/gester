package com.demn.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
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
    @Query("SELECT * FROM PluginCacheDbo WHERE pluginUuid = :uuid")
    suspend fun getPluginWithCommands(uuid: UUID): PluginWithCommandsDbo?

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
}