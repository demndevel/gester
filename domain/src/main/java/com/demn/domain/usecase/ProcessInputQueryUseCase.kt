package com.demn.domain.usecase

import com.demn.domain.models.PluginCommand
import com.demn.domain.pluginmanagement.PluginRepository
import com.demn.domain.settings.PluginAvailabilityRepository
import com.demn.domain.models.Plugin
import com.demn.plugincore.operationresult.CommandOperationResult
import com.demn.plugincore.operationresult.OperationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class ProcessInputQueryUseCase(
    private val pluginRepository: PluginRepository,
    private val operationResultSorter: OperationResultSorterUseCase,
    private val pluginAvailabilityRepository: PluginAvailabilityRepository,
    private val commandSearcherUseCase: CommandSearcherUseCase,
) {
    suspend operator fun invoke(plugins: List<Plugin>, inputQuery: String, onError: () -> Unit): List<OperationResult> {
        val availablePlugins = plugins
            .filter { pluginAvailabilityRepository.checkPluginEnabled(it.metadata.pluginUuid) }

        val resultsByAnyInput = getResultsByAnyInput(availablePlugins, inputQuery, onError)

        val commandResults = commandSearcherUseCase(inputQuery)
            .map(PluginCommand::toOperationResult)

        return operationResultSorter(inputQuery, resultsByAnyInput + commandResults)
    }

    private suspend fun getResultsByAnyInput(
        availablePlugins: List<Plugin>,
        inputQuery: String,
        onError: () -> Unit
    ): List<OperationResult> = withContext(Dispatchers.IO) {
        val resultsByAnyInputDefers = availablePlugins
            .filter { it.metadata.consumeAnyInput }
            .map { plugin ->
                async {
                    pluginRepository.getAnyResults(inputQuery, plugin, onError)
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
