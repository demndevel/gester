package com.demn.pluginloading

import com.demn.domain.models.*
import com.demn.domain.pluginmanagement.PluginRepository
import com.demn.plugincore.operationresult.OperationResult
import com.demn.domain.pluginproviders.ExternalPluginsProvider
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
    private val externalPluginsProvider: ExternalPluginsProvider,
) : PluginRepository {
    private suspend fun getExternalPlugins(): GetExternalPluginListInvocationResult {
        return externalPluginsProvider.getPluginList()
    }

    override suspend fun getAnyResults(input: String, plugin: Plugin): List<OperationResult> {
        return when (plugin) {
            is Plugin -> getAnyResultsWithExternalPlugin(plugin, input)

            else -> emptyList()
        }
    }

    override suspend fun getAllFallbackCommands(): List<PluginFallbackCommand> {
        return getAllExternalFallbackCommands()
    }

    private suspend fun getAllExternalFallbackCommands(): List<PluginFallbackCommand> {
        return externalPluginsProvider.getAllPluginFallbackCommands()
    }

    private suspend fun getAnyResultsWithExternalPlugin(
        plugin: Plugin,
        input: String
    ): List<OperationResult> {
        return externalPluginsProvider.executeAnyInput(
            input = input,
            pluginService = plugin.pluginService,
        )
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

        when (plugin) {
            is Plugin -> {
                invokeExternalPluginFallbackCommand(input, commandUuid, plugin)
            }
        }
    }

    override suspend fun getAllCommands(): List<PluginCommand> {
        return externalPluginsProvider.getAllPluginCommands()
    }

    override suspend fun invokeCommand(commandUuid: UUID, pluginId: String) {
        val plugin = getPluginList().plugins
            .find { it.metadata.pluginId == pluginId }

        if (plugin == null) return

        when (plugin) {
            is Plugin -> externalPluginsProvider.executeCommand(commandUuid, pluginId)
        }
    }

    private suspend fun invokeExternalPluginFallbackCommand(
        input: String,
        commandUuid: UUID,
        plugin: Plugin
    ) {
        externalPluginsProvider.executeFallbackCommand(
            input = input,
            fallbackCommandUuid = commandUuid,
            pluginService = plugin.pluginService
        )
    }

    override suspend fun getPluginList(): GetPluginListInvocationResult {
        val externalPlugins = getExternalPlugins()

        return GetPluginListInvocationResult(
            plugins = externalPlugins.plugins,
            pluginErrors = externalPlugins.pluginErrors
        )
    }
}