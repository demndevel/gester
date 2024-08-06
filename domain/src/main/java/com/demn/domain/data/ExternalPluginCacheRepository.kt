package com.demn.domain.data

import com.demn.domain.models.PluginCommand
import com.demn.plugincore.PluginMetadata
import java.util.UUID

interface ExternalPluginCacheRepository {
    suspend fun getAllPlugins(): List<PluginCache>

    suspend fun getPluginCache(uuid: UUID): PluginCache?

    suspend fun updatePluginCache(pluginCache: PluginCache)
}

data class PluginCache(
    val pluginMetadata: PluginMetadata,
    val commands: List<PluginCommand>
)