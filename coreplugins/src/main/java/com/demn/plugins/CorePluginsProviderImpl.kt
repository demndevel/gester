package com.demn.plugins

import com.demn.plugincore.operation_result.OperationResult
import com.demn.plugins.core_plugins.AppSearchingPlugin
import java.util.UUID

interface CorePluginsProvider {
    fun getPlugins(): List<CorePlugin>

    suspend fun invokeAnyInput(input: String, uuid: UUID): List<OperationResult>

    suspend fun invokePluginCommand(
        input: String,
        pluginCommandId: UUID,
        uuid: UUID
    ): List<OperationResult>
}

class MockCorePluginsProvider : CorePluginsProvider {
    override fun getPlugins(): List<CorePlugin> {
        return emptyList()
    }

    override suspend fun invokeAnyInput(input: String, uuid: UUID): List<OperationResult> {
        return emptyList()
    }

    override suspend fun invokePluginCommand(input: String, pluginCommandId: UUID, uuid: UUID): List<OperationResult> {
        return emptyList()
    }
}

class CorePluginsProviderImpl : CorePluginsProvider {
    val plugins = listOf(AppSearchingPlugin())

    override fun getPlugins(): List<CorePlugin> {
        return plugins
    }

    override suspend fun invokeAnyInput(input: String, uuid: UUID): List<OperationResult> {
        val plugin = plugins
            .find { it.metadata.pluginUuid == uuid }

        if (plugin == null) return emptyList()

        return plugin.invokeAnyInput(input)
    }

    override suspend fun invokePluginCommand(input: String, pluginCommandId: UUID, uuid: UUID): List<OperationResult> {
        val plugin = plugins
            .find { it.metadata.pluginUuid == uuid }

        if (plugin == null) return emptyList()

        return plugin.invokePluginCommand(input, pluginCommandId)
    }
}