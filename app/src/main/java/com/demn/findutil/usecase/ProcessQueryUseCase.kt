package com.demn.findutil.usecase

import com.demn.findutil.app_settings.AppSettingsRepository
import com.demn.plugincore.Plugin
import com.demn.plugincore.PluginCommand
import com.demn.plugincore.operation_result.OperationResult
import com.demn.pluginloading.PluginInvocationResult
import com.demn.pluginloading.PluginRepository
import kotlinx.coroutines.*
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
    private val pluginRepository: PluginRepository,
    private val appSettingsRepository: AppSettingsRepository
) : ProcessQueryUseCase {
    override suspend fun invoke(plugins: List<Plugin>, inputQuery: String): List<OperationResult> {
        val availablePlugins = plugins
            .filter { appSettingsRepository.checkPluginEnabled(it.metadata.pluginUuid) }
        val allCommands = availablePlugins
            .flatMap { it.metadata.commands }
        val commands = filterMatchedCommands(allCommands, inputQuery)

        val resultsByAnyInput = getResultsByAnyInput(availablePlugins, inputQuery)
        val resultsByCommand = getResultsByCommand(commands, inputQuery)

        return sortResults(results = resultsByAnyInput + resultsByCommand)
    }

    private suspend fun getResultsByCommand(
        commands: List<PluginCommand>,
        inputQuery: String
    ) = coroutineScope {
        val pluginInvocationResultsByCommand = commands
            .map {
                async { pluginRepository.invokePluginCommand(inputQuery, it.id) }
            }
            .awaitAll()

        pluginInvocationResultsByCommand
            .filterIsInstance<SuccessfulOperationResultInvocation>()
            .flatMap { it.value }
    }

    private fun filterMatchedCommands(
        allCommands: List<PluginCommand>,
        inputQuery: String
    ) = allCommands
        .filter { command ->
            val pattern = Pattern.compile(command.triggerRegex)
            val matcher = pattern.matcher(inputQuery)

            matcher.find()
        }

    private suspend fun getResultsByAnyInput(
        availablePlugins: List<Plugin>,
        inputQuery: String
    ) = coroutineScope {
        val resultsByAnyInputDefers = availablePlugins
            .filter { it.metadata.consumeAnyInput }
            .map {
                async { invokeAnyResults(it, inputQuery) }
            }

        resultsByAnyInputDefers.awaitAll()
            .flatten()
    }

    private fun sortResults(results: List<OperationResult>): List<OperationResult> =
        results.sortedWith(OperationResultComparator())

    private suspend fun invokeAnyResults(
        plugin: Plugin,
        inputQuery: String
    ): List<OperationResult> =
        pluginRepository.getAnyResults(inputQuery, plugin)
}

private typealias SuccessfulOperationResultInvocation = PluginInvocationResult.Success<List<OperationResult>>