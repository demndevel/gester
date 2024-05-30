package com.demn.plugins

import com.demn.plugincore.PluginMetadata
import com.demn.plugincore.operation_result.OperationResult
import java.util.UUID

interface CorePlugin {
    val metadata: PluginMetadata

    fun invokeAnyInput(input: String): List<OperationResult>

    fun invokePluginCommand(input: String, uuid: UUID): List<OperationResult>
}