package com.demn.domain.data

import com.demn.domain.models.ExternalPlugin
import com.demn.domain.models.PluginCommand
import com.demn.plugincore.Plugin
import java.util.UUID

interface PluginCommandCacheRepository {
    suspend fun getAllPlugins(): List<PluginCache>

    suspend fun updatePluginCache(pluginCache: PluginCache)
}

data class PluginCache(
    val pluginUuid: UUID,
    val name: String,
    val description: String? = null,
    val version: String,
    val consumeAnyInput: Boolean = false,
    val commands: List<PluginCommand>
)