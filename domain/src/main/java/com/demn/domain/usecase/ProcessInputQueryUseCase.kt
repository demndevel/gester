package com.demn.domain.usecase

import com.demn.domain.models.PluginCommand
import com.demn.domain.pluginmanagement.PluginRepository
import com.demn.domain.settings.PluginAvailabilityRepository
import com.demn.domain.models.Plugin
import com.demn.plugincore.operationresult.CommandOperationResult
import com.demn.plugincore.operationresult.OperationResult
import kotlinx.coroutines.*

class ProcessInputQueryUseCase(
    private val pluginRepository: PluginRepository,
    private val operationResultSorter: OperationResultSorterUseCase,
    private val pluginAvailabilityRepository: PluginAvailabilityRepository,
    private val commandSearcherUseCase: CommandSearcherUseCase,
) {
    suspend operator fun invoke(plugins: List<Plugin>, inputQuery: String): List<OperationResult> {
        val availablePlugins = plugins
            .filter { pluginAvailabilityRepository.checkPluginEnabled(it.metadata.pluginUuid) }

        val resultsByAnyInput = getResultsByAnyInput(availablePlugins, inputQuery)

        val commandResults = commandSearcherUseCase(inputQuery)
            .map(PluginCommand::toOperationResult)

        return operationResultSorter(inputQuery, resultsByAnyInput + commandResults)
    }

    private suspend fun getResultsByAnyInput(
        availablePlugins: List<Plugin>,
        inputQuery: String,
    ): List<OperationResult> = coroutineScope {
        val resultsByAnyInputDefers = availablePlugins
            .filter { it.metadata.consumeAnyInput }
            .map { plugin ->
                async {
                    pluginRepository.getAnyResults(inputQuery, plugin)
                }
            }

        resultsByAnyInputDefers.awaitAll()
            .flatten()
    }
}

private fun PluginCommand.toOperationResult(): OperationResult {
    return CommandOperationResult(
        uuid = uuid,
        pluginUuid = pluginUuid,
        iconUri = iconUri,
        name = name
    )
}
