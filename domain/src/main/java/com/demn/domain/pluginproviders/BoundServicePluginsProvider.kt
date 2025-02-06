package com.demn.domain.pluginproviders

import com.demn.domain.models.*
import io.github.demndevel.gester.core.operationresult.OperationResult
import io.github.demndevel.gester.core.parcelables.PluginSetting
import java.util.UUID

interface BoundServicePluginsProvider {
    /**
     * Caches new or updated plugins & returns plugins from cache
     */
    suspend fun getPluginList(): GetBoundServicePluginListInvocationResult

    /**
     * Gets plugin settings list via IPC
     */
    suspend fun getPluginSettings(plugin: Plugin): List<PluginSetting>

    /**
     * Gets all plugin commands from cache
     */
    suspend fun getAllPluginCommands(): List<PluginCommand>

    /**
     * Gets all fallback plugin commands from cache
     */
    suspend fun getAllPluginFallbackCommands(): List<PluginFallbackCommand>

    suspend fun executeFallbackCommand(
        input: String,
        fallbackCommandUuid: UUID,
        pluginService: PluginService
    )

    suspend fun executeCommand(
        uuid: UUID,
        pluginId: String
    )

    suspend fun executeAnyInput(
        input: String,
        pluginService: PluginService,
    ): List<OperationResult>

    suspend fun setPluginSetting(
        plugin: Plugin,
        settingUuid: UUID,
        newValue: String
    )
}
