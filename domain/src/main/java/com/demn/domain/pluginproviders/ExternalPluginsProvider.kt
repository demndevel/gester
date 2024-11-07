package com.demn.domain.pluginproviders

import com.demn.domain.models.*
import com.demn.plugincore.parcelables.PluginSetting
import com.demn.plugincore.operationresult.OperationResult
import java.util.UUID

interface ExternalPluginsProvider {
    /**
     * Caches new or updated plugins & returns plugins from cache
     */
    suspend fun getPluginList(): GetExternalPluginListInvocationResult

    /**
     * Gets plugin settings list via IPC
     */
    suspend fun getPluginSettings(externalPlugin: ExternalPlugin): List<PluginSetting>

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
        externalPlugin: ExternalPlugin,
        settingUuid: UUID,
        newValue: String
    )
}
