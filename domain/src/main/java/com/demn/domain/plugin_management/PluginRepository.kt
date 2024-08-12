package com.demn.domain.plugin_management

import com.demn.domain.models.PluginCommand
import com.demn.domain.models.PluginFallbackCommand
import com.demn.domain.models.Plugin
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

    suspend fun getAnyResults(input: String, plugin: Plugin, onError: () -> Unit): List<OperationResult>

    suspend fun getAllFallbackCommands(): List<PluginFallbackCommand>
}