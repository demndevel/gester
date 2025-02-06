package com.demn.pluginloading

import com.demn.domain.models.*
import com.demn.domain.pluginmanagement.PluginRepository
import com.demn.domain.pluginproviders.BoundServicePluginsProvider
import io.github.demndevel.gester.core.operationresult.OperationResult
import java.util.UUID

class MockPluginRepository : PluginRepository {
    override suspend fun getPluginList(): GetPluginListInvocationResult {
        return GetPluginListInvocationResult(emptyList())
    }

    override suspend fun invokeFallbackCommand(input: String, commandUuid: UUID) = Unit

    override suspend fun getAllCommands(): List<PluginCommand> = emptyList()

    override suspend fun invokeCommand(commandUuid: UUID, pluginId: String) = Unit

    override suspend fun getAnyResults(input: String, plugin: Plugin): List<OperationResult> {
        return emptyList()
    }

    override suspend fun getAllFallbackCommands(): List<PluginFallbackCommand> = emptyList()
}

class PluginRepositoryImpl(
    private val boundServicePluginsProvider: BoundServicePluginsProvider,
) : PluginRepository {
    override suspend fun getAnyResults(input: String, plugin: Plugin): List<OperationResult> {
        return boundServicePluginsProvider.executeAnyInput(
            input = input,
            pluginService = plugin.pluginService,
        )
    }

    override suspend fun getAllFallbackCommands(): List<PluginFallbackCommand> {
        return boundServicePluginsProvider.getAllPluginFallbackCommands()
    }

    override suspend fun invokeFallbackCommand(input: String, commandUuid: UUID) {
        val commandPluginUuid = getAllFallbackCommands()
            .find { it.uuid == commandUuid }
            ?.pluginId

        val plugin = getPluginList().plugins
            .find {
                it.metadata.pluginId == commandPluginUuid
            }

        if (plugin == null) {
            return
        }

        boundServicePluginsProvider.executeFallbackCommand(
            input = input,
            fallbackCommandUuid = commandUuid,
            pluginService = plugin.pluginService
        )
    }

    override suspend fun getAllCommands(): List<PluginCommand> {
        return boundServicePluginsProvider.getAllPluginCommands()
    }

    override suspend fun invokeCommand(commandUuid: UUID, pluginId: String) {
        val plugin = getPluginList().plugins
            .find { it.metadata.pluginId == pluginId }

        if (plugin == null) return

        boundServicePluginsProvider.executeCommand(commandUuid, pluginId)
    }

    override suspend fun getPluginList(): GetPluginListInvocationResult {
        val getAllPluginsInvocationResult = boundServicePluginsProvider.getPluginList()

        return GetPluginListInvocationResult(
            plugins = getAllPluginsInvocationResult.plugins,
            pluginErrors = getAllPluginsInvocationResult.pluginErrors
        )
    }
}
