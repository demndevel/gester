package com.demn.pluginloading

import com.demn.domain.models.*
import com.demn.domain.pluginmanagement.PluginRepository
import com.demn.plugincore.operationresult.OperationResult
import com.demn.domain.pluginproviders.CorePluginsProvider
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
    private val corePluginsProvider: CorePluginsProvider,
    private val externalPluginsProvider: ExternalPluginsProvider,
) : PluginRepository {
    private suspend fun getExternalPlugins(): GetExternalPluginListInvocationResult {
        return externalPluginsProvider.getPluginList()
    }

    override suspend fun getAnyResults(input: String, plugin: Plugin): List<OperationResult> {
        return when (plugin) {
            is ExternalPlugin -> getAnyResultsWithExternalPlugin(plugin, input)

            is BuiltInPlugin -> getAnyResultsWithBuiltInPlugin(input, plugin)

            else -> emptyList()
        }
    }

    override suspend fun getAllFallbackCommands(): List<PluginFallbackCommand> {
        return getAllExternalFallbackCommands() + getAllBuiltInFallbackCommands()
    }

    private suspend fun getAllExternalFallbackCommands(): List<PluginFallbackCommand> {
        return externalPluginsProvider.getAllPluginFallbackCommands()
    }

    private suspend fun getAllBuiltInFallbackCommands(): List<PluginFallbackCommand> {
        return corePluginsProvider.getAllPluginFallbackCommands()
    }

    private suspend fun getAnyResultsWithExternalPlugin(
        plugin: ExternalPlugin,
        input: String
    ): List<OperationResult> {
        return externalPluginsProvider.executeAnyInput(
            input = input,
            pluginService = plugin.pluginService,
        )
    }

    private suspend fun getAnyResultsWithBuiltInPlugin(
        input: String,
        plugin: BuiltInPlugin,
    ): List<OperationResult> {
        val results = corePluginsProvider.invokeAnyInput(input, plugin.metadata.pluginId)

        return results
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
            is ExternalPlugin -> {
                invokeExternalPluginFallbackCommand(input, commandUuid, plugin)
            }

            is BuiltInPlugin -> {
                invokeBuiltInPluginFallbackCommand(input, commandUuid, plugin)
            }
        }
    }

    override suspend fun getAllCommands(): List<PluginCommand> {
        return corePluginsProvider.getAllPluginCommands() + externalPluginsProvider.getAllPluginCommands()
    }

    override suspend fun invokeCommand(commandUuid: UUID, pluginId: String) {
        val plugin = getPluginList().plugins
            .find { it.metadata.pluginId == pluginId }

        if (plugin == null) return

        when (plugin) {
            is BuiltInPlugin -> corePluginsProvider.invokePluginCommand(commandUuid, pluginId)

            is ExternalPlugin -> externalPluginsProvider.executeCommand(commandUuid, pluginId)
        }
    }

    private suspend fun invokeBuiltInPluginFallbackCommand(
        input: String,
        commandUuid: UUID,
        plugin: BuiltInPlugin
    ) {
        corePluginsProvider.invokePluginFallbackCommand(
            input = input,
            pluginFallbackCommandId = commandUuid,
            pluginId = plugin.metadata.pluginId
        )
    }

    private suspend fun invokeExternalPluginFallbackCommand(
        input: String,
        commandUuid: UUID,
        plugin: ExternalPlugin
    ) {
        externalPluginsProvider.executeFallbackCommand(
            input = input,
            fallbackCommandUuid = commandUuid,
            pluginService = plugin.pluginService
        )
    }

    override suspend fun getPluginList(): GetPluginListInvocationResult {
        val externalPlugins = getExternalPlugins()
        val corePlugins = corePluginsProvider.getPlugins()

        return GetPluginListInvocationResult(
            plugins = externalPlugins.plugins + corePlugins,
            pluginErrors = externalPlugins.pluginErrors
        )
    }
}