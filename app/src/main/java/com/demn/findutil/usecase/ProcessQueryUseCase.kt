package com.demn.findutil.usecase

import com.demn.plugincore.Plugin
import com.demn.plugincore.operation_result.OperationResult
import com.demn.pluginloading.PluginInvocationResult
import com.demn.pluginloading.PluginRepository
import java.util.regex.Pattern

interface ProcessQueryUseCase {
    suspend fun invoke(plugins: List<Plugin>, inputQuery: String): List<OperationResult>
}

class MockProcessQueryUseCaseImpl : ProcessQueryUseCase {
    override suspend fun invoke(plugins: List<Plugin>, inputQuery: String): List<OperationResult> {
        return emptyList()
    }
}

class ProcessQueryUseCaseImpl(
    private val pluginRepository: PluginRepository
) : ProcessQueryUseCase {
    override suspend fun invoke(plugins: List<Plugin>, inputQuery: String): List<OperationResult> {
        val resultsByAnyInput = plugins
            .filter { it.metadata.consumeAnyInput }
            .map { invokeAnyResults(it, inputQuery) }
            .flatten()

        val allCommands = pluginRepository.getAllPluginCommands()

        val commands = allCommands
            .filter { command ->
                val pattern = Pattern.compile(command.triggerRegex)
                val matcher = pattern.matcher(inputQuery)

                matcher.find()
            }

        val resultsByCommand = commands
            .mapNotNull {
                val invocationResult = pluginRepository.invokePluginCommand(inputQuery, it.id)
                if (invocationResult is PluginInvocationResult.Success) {
                    return@mapNotNull invocationResult.value
                }
                return@mapNotNull null
            }
            .flatten()

        return resultsByAnyInput + resultsByCommand
    }

    private suspend fun invokeAnyResults(
        plugin: Plugin,
        inputQuery: String
    ): List<OperationResult> {
        return pluginRepository.getAnyResults(inputQuery, plugin)
    }
}