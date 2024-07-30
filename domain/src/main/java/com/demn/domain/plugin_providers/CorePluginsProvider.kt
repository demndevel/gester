package com.demn.domain.plugin_providers

import com.demn.domain.models.BuiltInPlugin
import com.demn.domain.models.PluginCommand
import com.demn.plugincore.PluginSetting
import com.demn.plugincore.operation_result.OperationResult
import java.util.UUID

interface CorePluginsProvider {
    fun getPlugins(): List<BuiltInPlugin>

    suspend fun invokeAnyInput(input: String, uuid: UUID): List<OperationResult>

    fun getPluginCommands(plugin: BuiltInPlugin): List<PluginCommand>

    fun getAllPluginCommands(): List<PluginCommand>

    fun invokePluginCommand(commandUuid: UUID, pluginUuid: UUID)

    fun invokePluginFallbackCommand(
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