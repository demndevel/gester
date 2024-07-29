package com.demn.domain.usecase

import com.demn.domain.data.PluginCacheRepository
import com.demn.domain.plugin_management.OperationResultSorter
import com.demn.domain.plugin_management.PluginRepository
import com.demn.domain.settings.PluginAvailabilityRepository
import com.demn.plugincore.Plugin
import com.demn.plugincore.operation_result.OperationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class ProcessInputQueryUseCase(
    private val pluginRepository: PluginRepository,
    private val operationResultSorter: OperationResultSorter,
    private val pluginAvailabilityRepository: PluginAvailabilityRepository,
    private val pluginCacheRepository: PluginCacheRepository,
) {
    suspend operator fun invoke(plugins: List<Plugin>, inputQuery: String): List<OperationResult> {
        val availablePlugins = plugins
            .filter { pluginAvailabilityRepository.checkPluginEnabled(it.metadata.pluginUuid) }

        val resultsByAnyInput = getResultsByAnyInput(availablePlugins, inputQuery)

        pluginCacheRepository.getAllPlugins() // TODO

        return operationResultSorter.sort(resultsByAnyInput)
    }

    private suspend fun getResultsByAnyInput(
        availablePlugins: List<Plugin>,
        inputQuery: String
    ): List<OperationResult> = withContext(Dispatchers.IO) {
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