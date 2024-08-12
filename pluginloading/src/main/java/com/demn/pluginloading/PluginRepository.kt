package com.demn.pluginloading

import com.demn.domain.models.ExternalPlugin
import com.demn.domain.plugin_management.PluginRepository
import com.demn.domain.models.Plugin
import com.demn.plugincore.operation_result.OperationResult
import com.demn.domain.models.BuiltInPlugin
import com.demn.domain.models.PluginCommand
import com.demn.domain.models.PluginFallbackCommand
import com.demn.domain.plugin_providers.CorePluginsProvider
import com.demn.domain.plugin_providers.ExternalPluginsProvider
import java.util.UUID

class MockPluginRepository : PluginRepository {
    override suspend fun getPluginList(): List<Plugin> {
        return emptyList()
    }

    override suspend fun invokeFallbackCommand(input: String, commandUuid: UUID) = Unit

    override suspend fun getAllCommands(): List<PluginCommand> = emptyList()

    override suspend fun invokeCommand(commandUuid: UUID, pluginUuid: UUID) = Unit

    override suspend fun getAnyResults(input: String, plugin: Plugin, onError: () -> Unit): List<OperationResult> {
        return emptyList()
    }

    override suspend fun getAllFallbackCommands(): List<PluginFallbackCommand> = emptyList()
}

class PluginRepositoryImpl(
    private val corePluginsProvider: CorePluginsProvider,
    private val externalPluginsProvider: ExternalPluginsProvider,
) : PluginRepository {
    private suspend fun getExternalPlugins(): List<ExternalPlugin> {
        return externalPluginsProvider.getPluginList()
    }

    override suspend fun getAnyResults(input: String, plugin: Plugin, onError: () -> Unit): List<OperationResult> {
        return when (plugin) {
            is ExternalPlugin -> getAnyResultsWithExternalPlugin(plugin, input, onError = onError)

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
        input: String,
        onError: () -> Unit
    ): List<OperationResult> {
        return externalPluginsProvider.executeAnyInput(
            input = input,
            pluginService = plugin.pluginService,
            onError = onError
        )
    }

    private suspend fun getAnyResultsWithBuiltInPlugin(
        input: String,
        plugin: BuiltInPlugin,
    ): List<OperationResult> {
        val results = corePluginsProvider.invokeAnyInput(input, plugin.metadata.pluginUuid)

        return results
    }

    override suspend fun invokeFallbackCommand(input: String, commandUuid: UUID) {
        val commandPluginUuid = getAllFallbackCommands()
            .find { it.uuid == commandUuid }
            ?.pluginUuid

        val plugin = getPluginList()
            .find {
                it.metadata.pluginUuid == commandPluginUuid
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

    override suspend fun invokeCommand(commandUuid: UUID, pluginUuid: UUID) {
        val plugin = getPluginList()
            .find { it.metadata.pluginUuid == pluginUuid }

        if (plugin == null) return

        when (plugin) {
            is BuiltInPlugin -> corePluginsProvider.invokePluginCommand(commandUuid, pluginUuid)

            is ExternalPlugin -> externalPluginsProvider.executeCommand(commandUuid, pluginUuid)
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
            pluginUuid = plugin.metadata.pluginUuid
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

    override suspend fun getPluginList(): List<Plugin> {
        val externalPlugins = getExternalPlugins()
        val corePlugins = corePluginsProvider.getPlugins()

        return externalPlugins + corePlugins
    }
}