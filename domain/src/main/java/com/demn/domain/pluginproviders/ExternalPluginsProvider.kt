package com.demn.domain.pluginproviders

import com.demn.domain.models.ExternalPlugin
import com.demn.domain.models.PluginCommand
import com.demn.domain.models.PluginFallbackCommand
import com.demn.domain.models.PluginService
import com.demn.plugincore.parcelables.PluginSetting
import com.demn.plugincore.operationresult.OperationResult
import java.util.UUID

interface ExternalPluginsProvider {
    /**
     * Caches new or updated plugins & returns plugins from cache
     */
    suspend fun getPluginList(): List<ExternalPlugin>

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
        pluginUuid: UUID
    )

    suspend fun executeAnyInput(
        input: String,
        pluginService: PluginService,
        onError: () -> Unit
    ): List<OperationResult>

    suspend fun setPluginSetting(
        externalPlugin: ExternalPlugin,
        settingUuid: UUID,
        newValue: String
    )
}
