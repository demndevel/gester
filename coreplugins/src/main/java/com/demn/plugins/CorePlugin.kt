package com.demn.plugins

import com.demn.plugincore.PluginMetadata
import com.demn.plugincore.PluginSetting
import com.demn.plugincore.operation_result.OperationResult
import java.util.UUID

interface CorePlugin {
    val metadata: PluginMetadata

    fun getPluginSettings(): List<PluginSetting>

    fun invokeAnyInput(input: String): List<OperationResult>

    fun invokePluginFallbackCommand(input: String, uuid: UUID)
}