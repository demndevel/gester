package com.demn.pluginloading

import com.demn.plugincore.Plugin
import com.demn.plugincore.PluginCommand
import com.demn.plugincore.operation_result.OperationResult
import com.demn.plugins.BuiltInPlugin
import com.demn.plugins.CorePluginsProvider
import java.util.UUID

sealed interface PluginInvocationResult<T> {
    data class Success<T>(val value: T) :
        PluginInvocationResult<T>

    class Failure<T> : PluginInvocationResult<T>
}

interface PluginRepository {
    suspend fun getPluginList(): List<Plugin>

    suspend fun invokePluginCommand(
        input: String,
        commandUuid: UUID
    ): PluginInvocationResult<List<OperationResult>>

    suspend fun getAnyResults(input: String, plugin: Plugin): List<OperationResult>

    suspend fun getAllPluginCommands(): List<PluginCommand>
}

class MockPluginRepository : PluginRepository {
    override suspend fun getPluginList(): List<Plugin> {
        return emptyList()
    }

    override suspend fun invokePluginCommand(
        input: String,
        commandUuid: UUID
    ): PluginInvocationResult<List<OperationResult>> {
        return PluginInvocationResult.Success(value = emptyList())
    }

    override suspend fun getAnyResults(input: String, plugin: Plugin): List<OperationResult> {
        return emptyList()
    }

    override suspend fun getAllPluginCommands(): List<PluginCommand> {
        return emptyList()
    }
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

    override suspend fun getAllPluginCommands(): List<PluginCommand> {
        val commands = getPluginList()
            .map { it.metadata.commands }
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

    override suspend fun invokePluginCommand(
        input: String,
        commandUuid: UUID
    ): PluginInvocationResult<List<OperationResult>> {
        val plugin = getPluginList()
            .find { plugin ->
                plugin.metadata.commands.any {
                    it.id == commandUuid
                }
            }

        if (plugin == null) {
            return PluginInvocationResult.Failure()
        }

        val results = when (plugin) {
            is ExternalPlugin -> {
                invokeExternalPluginCommand(input, commandUuid, plugin)
            }

            is BuiltInPlugin -> {
                invokeBuiltInPluginCommand(input, commandUuid, plugin)
            }

            else -> emptyList()
        }

        return PluginInvocationResult.Success(results)
    }

    private suspend fun invokeExternalPluginCommand(
        input: String,
        commandUuid: UUID,
        plugin: ExternalPlugin
    ): List<OperationResult> {
        return externalPluginsProvider.executeCommand(
            input = input,
            commandUuid = commandUuid,
            pluginService = plugin.pluginService
        )
    }

    private suspend fun invokeBuiltInPluginCommand(
        input: String,
        commandUuid: UUID,
        plugin: BuiltInPlugin,
    ): List<OperationResult> {
        val results = corePluginsProvider.invokePluginCommand(
            input = input,
            pluginCommandId = commandUuid,
            pluginUuid = plugin.metadata.pluginUuid
        )

        return results
    }

    override suspend fun getPluginList(): List<Plugin> {
        val externalPlugins = fillExternalPlugins()
        val corePlugins = corePluginsProvider.getPlugins()

        return externalPlugins + corePlugins
    }
}