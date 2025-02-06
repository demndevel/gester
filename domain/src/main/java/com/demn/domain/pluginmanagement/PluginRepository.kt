package com.demn.domain.pluginmanagement

import com.demn.domain.models.GetPluginListInvocationResult
import com.demn.domain.models.PluginCommand
import com.demn.domain.models.PluginFallbackCommand
import com.demn.domain.models.Plugin
import io.github.demndevel.gester.core.operationresult.OperationResult
import java.util.UUID

interface PluginRepository {
    suspend fun getPluginList(): GetPluginListInvocationResult

    suspend fun invokeFallbackCommand(
        input: String,
        commandUuid: UUID
    )

    suspend fun getAllCommands(): List<PluginCommand>

    suspend fun invokeCommand(commandUuid: UUID, pluginId: String)

    suspend fun getAnyResults(input: String, plugin: Plugin): List<OperationResult>

    suspend fun getAllFallbackCommands(): List<PluginFallbackCommand>
}
