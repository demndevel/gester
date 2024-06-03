package com.demn.plugins

import android.content.Context
import com.demn.plugincore.operation_result.OperationResult
import com.demn.plugins.core_plugins.AppSearchingPlugin
import com.demn.plugins.core_plugins.CurrenciesPlugin
import java.util.UUID

interface CorePluginsProvider {
    fun getPlugins(): List<BuiltInPlugin>

    suspend fun invokeAnyInput(input: String, uuid: UUID): List<OperationResult>

    suspend fun invokePluginCommand(
        input: String,
        pluginCommandId: UUID,
        pluginUuid: UUID
    ): List<OperationResult>
}

class MockCorePluginsProvider : CorePluginsProvider {
    override fun getPlugins(): List<BuiltInPlugin> {
        return emptyList()
    }

    override suspend fun invokeAnyInput(input: String, uuid: UUID): List<OperationResult> {
        return emptyList()
    }

    override suspend fun invokePluginCommand(
        input: String,
        pluginCommandId: UUID,
        pluginUuid: UUID
    ): List<OperationResult> {
        return emptyList()
    }
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
}