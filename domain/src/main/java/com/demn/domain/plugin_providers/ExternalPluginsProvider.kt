package com.demn.domain.plugin_providers

import com.demn.domain.models.ExternalPlugin
import com.demn.domain.models.PluginCommand
import com.demn.domain.models.PluginService
import com.demn.plugincore.PluginMetadata
import com.demn.plugincore.PluginSetting
import com.demn.plugincore.operation_result.OperationResult
import java.util.UUID

interface ExternalPluginsProvider {
    suspend fun getPluginList(): List<ExternalPlugin>

    suspend fun executeFallbackCommand(
        input: String,
        fallbackCommandUuid: UUID,
        pluginService: PluginService
    )

    suspend fun getPluginCommands(plugin: ExternalPlugin): List<PluginCommand>

    suspend fun executeCommand(
        uuid: UUID,
        pluginUuid: UUID
    )

    suspend fun executeAnyInput(
        input: String,
        pluginService: PluginService
    ): List<OperationResult>

    suspend fun getPluginData(pluginService: PluginService): PluginMetadata

    suspend fun getPluginSettings(externalPlugin: ExternalPlugin): List<PluginSetting>

    suspend fun setPluginSetting(
        externalPlugin: ExternalPlugin,
        settingUuid: UUID,
        newValue: String
    )

    suspend fun getPluginCommands(): List<PluginCommand>
}
