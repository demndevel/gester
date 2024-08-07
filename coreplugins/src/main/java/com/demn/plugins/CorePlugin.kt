package com.demn.plugins

import com.demn.domain.models.PluginCommand
import com.demn.domain.models.PluginFallbackCommand
import com.demn.plugincore.PluginMetadata
import com.demn.plugincore.PluginSetting
import com.demn.plugincore.operation_result.OperationResult
import java.util.UUID

interface CorePlugin {
    val metadata: PluginMetadata

    fun invokeCommand(uuid: UUID)

    fun getPluginCommands(): List<PluginCommand>

    fun getPluginFallbackCommands(): List<PluginFallbackCommand>

    fun getPluginSettings(): List<PluginSetting>

    fun invokeAnyInput(input: String): List<OperationResult>

    fun invokePluginFallbackCommand(input: String, uuid: UUID)
}