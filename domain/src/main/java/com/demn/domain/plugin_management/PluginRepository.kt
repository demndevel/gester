package com.demn.domain.plugin_management

import com.demn.domain.models.PluginCommand
import com.demn.plugincore.Plugin
import com.demn.plugincore.PluginFallbackCommand
import com.demn.plugincore.operation_result.OperationResult
import java.util.UUID

interface PluginRepository {
    suspend fun getPluginList(): List<Plugin>

    suspend fun invokeFallbackCommand(
        input: String,
        commandUuid: UUID
    )

    suspend fun getAllCommands(): List<PluginCommand>

    suspend fun invokeCommand(commandUuid: UUID, pluginUuid: UUID)

    suspend fun getAnyResults(input: String, plugin: Plugin): List<OperationResult>

    suspend fun getAllFallbackCommands(): List<PluginFallbackCommand>
}