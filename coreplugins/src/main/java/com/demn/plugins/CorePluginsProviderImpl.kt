package com.demn.plugins

import com.demn.plugincore.PluginSetting
import com.demn.plugincore.operation_result.OperationResult
import java.util.UUID

interface CorePluginsProvider {
    fun getPlugins(): List<BuiltInPlugin>

    suspend fun invokeAnyInput(input: String, uuid: UUID): List<OperationResult>

    suspend fun invokePluginCommand(
        input: String,
        pluginCommandId: UUID,
        pluginUuid: UUID
    ): List<OperationResult>

    suspend fun getPluginSettings(
        pluginUuid: UUID
    ): List<PluginSetting>

    suspend fun setPluginSetting(
        // TODO
    )
}

class CorePluginsProviderImpl(
    private val plugins: List<CorePlugin>
) :
    CorePluginsProvider {
    override fun getPlugins(): List<BuiltInPlugin> {
        return plugins
            .map { BuiltInPlugin(it.metadata) }
    }

    override suspend fun invokeAnyInput(input: String, uuid: UUID): List<OperationResult> {
        val plugin = plugins
            .find { it.metadata.pluginUuid == uuid }

        if (plugin == null) return emptyList()

        return plugin.invokeAnyInput(input)
    }

    override suspend fun invokePluginCommand(
        input: String,
        pluginCommandId: UUID,
        pluginUuid: UUID
    ): List<OperationResult> {
        val plugin = plugins
            .find { it.metadata.pluginUuid == pluginUuid }

        if (plugin == null) {
            return emptyList()
        }

        return plugin.invokePluginCommand(input, pluginCommandId)
    }

    override suspend fun getPluginSettings(pluginUuid: UUID): List<PluginSetting> {
        return emptyList() // TODO
    }

    override suspend fun setPluginSetting() {
        TODO("Not yet implemented")
    }
}