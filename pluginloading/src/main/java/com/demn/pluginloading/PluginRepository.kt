package com.demn.pluginloading

import com.demn.domain.models.ExternalPlugin
import com.demn.domain.plugin_management.PluginRepository
import com.demn.plugincore.Plugin
import com.demn.plugincore.PluginFallbackCommand
import com.demn.plugincore.operation_result.OperationResult
import com.demn.domain.models.BuiltInPlugin
import com.demn.domain.plugin_providers.CorePluginsProvider
import com.demn.domain.plugin_providers.ExternalPluginsProvider
import java.util.UUID

class MockPluginRepository : PluginRepository {
    override suspend fun getPluginList(): List<Plugin> {
        return emptyList()
    }

    override suspend fun invokeFallbackCommand(input: String, commandUuid: UUID) = Unit

    override suspend fun getAnyResults(input: String, plugin: Plugin): List<OperationResult> {
        return emptyList()
    }

    override suspend fun getAllFallbackCommands(): List<PluginFallbackCommand> = emptyList()
}

class PluginRepositoryImpl(
    private val corePluginsProvider: CorePluginsProvider,
    private val externalPluginsProvider: ExternalPluginsProvider,
) : PluginRepository {
    private suspend fun fillExternalPlugins(): List<ExternalPlugin> {
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
        val commands = getPluginList()
            .map { it.metadata.fallbackCommands }
            .flatten()

        return commands
    }

    private suspend fun getAnyResultsWithExternalPlugin(
        plugin: ExternalPlugin,
        input: String
    ): List<OperationResult> {
        return externalPluginsProvider.executeAnyInput(
            input = input,
            pluginService = plugin.pluginService
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
        val plugin = getPluginList()
            .find { plugin ->
                plugin.metadata.fallbackCommands.any { it.id == commandUuid }
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

    private fun invokeBuiltInPluginFallbackCommand(
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
        val externalPlugins = fillExternalPlugins()
        val corePlugins = corePluginsProvider.getPlugins()

        return externalPlugins + corePlugins
    }
}