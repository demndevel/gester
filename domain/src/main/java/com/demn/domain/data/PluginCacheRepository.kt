package com.demn.domain.data

import com.demn.domain.models.PluginCommand
import com.demn.domain.models.PluginFallbackCommand
import com.demn.plugincore.parcelables.PluginMetadata

interface PluginCacheRepository {
    suspend fun getAllPlugins(): List<PluginCache>

    suspend fun getPluginCache(id: String): PluginCache?

    suspend fun updatePluginCache(pluginCache: PluginCache)

    suspend fun removePluginCache(id: String)
}

data class PluginCache(
    val pluginMetadata: PluginMetadata,
    val commands: List<PluginCommand>,
    val fallbackCommands: List<PluginFallbackCommand>
)