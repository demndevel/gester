package com.demn.domain.plugin_providers

import com.demn.domain.models.BuiltInPlugin
import com.demn.domain.models.PluginCommand
import com.demn.domain.models.PluginFallbackCommand
import com.demn.plugincore.parcelables.PluginSetting
import com.demn.plugincore.operation_result.OperationResult
import java.util.UUID

interface CorePluginsProvider {
    fun getPlugins(): List<BuiltInPlugin>

    suspend fun invokeAnyInput(input: String, uuid: UUID): List<OperationResult>

    suspend fun getPluginCommands(plugin: BuiltInPlugin): List<PluginCommand>

    suspend fun getAllPluginCommands(): List<PluginCommand>

    suspend fun getAllPluginFallbackCommands(): List<PluginFallbackCommand>

    suspend fun invokePluginCommand(commandUuid: UUID, pluginUuid: UUID)

    suspend fun invokePluginFallbackCommand(
        input: String,
        pluginFallbackCommandId: UUID,
        pluginUuid: UUID,
    )

    suspend fun getPluginSettings(
        pluginUuid: UUID
    ): List<PluginSetting>

    suspend fun setPluginSetting(
        builtInPlugin: BuiltInPlugin,
        settingUuid: UUID,
        newValue: String
    )
}