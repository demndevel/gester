package com.demn.domain.pluginproviders

import com.demn.domain.models.BuiltInPlugin
import com.demn.domain.models.PluginCommand
import com.demn.domain.models.PluginFallbackCommand
import com.demn.plugincore.parcelables.PluginSetting
import com.demn.plugincore.operationresult.OperationResult
import java.util.UUID

interface CorePluginsProvider {
    fun getPlugins(): List<BuiltInPlugin>

    suspend fun invokeAnyInput(input: String, pluginId: String): List<OperationResult>

    suspend fun getPluginCommands(plugin: BuiltInPlugin): List<PluginCommand>

    suspend fun getAllPluginCommands(): List<PluginCommand>

    suspend fun getAllPluginFallbackCommands(): List<PluginFallbackCommand>

    suspend fun invokePluginCommand(commandUuid: UUID, pluginId: String)

    suspend fun invokePluginFallbackCommand(
        input: String,
        pluginFallbackCommandId: UUID,
        pluginId: String,
    )

    suspend fun getPluginSettings(
        pluginUuid: String
    ): List<PluginSetting>

    suspend fun setPluginSetting(
        builtInPlugin: BuiltInPlugin,
        settingUuid: UUID,
        newValue: String
    )
}