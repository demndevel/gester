package com.demn.plugins

import com.demn.domain.models.PluginCommand
import com.demn.domain.models.PluginFallbackCommand
import com.demn.plugincore.PluginMetadata
import com.demn.plugincore.PluginSetting
import com.demn.plugincore.operation_result.OperationResult
import java.util.UUID

interface CorePlugin {
    val metadata: PluginMetadata

    suspend fun invokeCommand(uuid: UUID)

    suspend fun getPluginCommands(): List<PluginCommand>

    suspend fun getPluginFallbackCommands(): List<PluginFallbackCommand>

    suspend fun getPluginSettings(): List<PluginSetting>

    suspend fun invokeAnyInput(input: String): List<OperationResult>

    suspend fun invokePluginFallbackCommand(input: String, uuid: UUID)
}