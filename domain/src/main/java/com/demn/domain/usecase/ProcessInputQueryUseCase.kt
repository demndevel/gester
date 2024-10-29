package com.demn.domain.usecase

import com.demn.domain.models.Plugin
import com.demn.domain.models.PluginCommand
import com.demn.domain.pluginmanagement.PluginRepository
import com.demn.domain.settings.PluginAvailabilityRepository
import com.demn.plugincore.operationresult.CommandOperationResult
import com.demn.plugincore.operationresult.OperationResult
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

data class ProcessInputQueryUseCaseConfig(
    val resultsDelay: Long
)

class ProcessInputQueryUseCase(
    private val pluginRepository: PluginRepository,
    private val operationResultSorter: OperationResultSorterUseCase,
    private val pluginAvailabilityRepository: PluginAvailabilityRepository,
    private val commandSearcherUseCase: CommandSearcherUseCase,
    private val config: ProcessInputQueryUseCaseConfig
) {
    operator fun invoke(plugins: List<Plugin>, inputQuery: String): Flow<List<OperationResult>> =
        channelFlow {
            coroutineScope {
                val availablePlugins = plugins
                    .filter { pluginAvailabilityRepository.checkPluginEnabled(it.metadata.pluginUuid) }
                val accumulator = mutableListOf<OperationResult>()
                var timeExpired = false

                accumulator += commandSearcherUseCase(inputQuery)
                    .map(PluginCommand::toOperationResult)

                val timerDeferrer = async {
                    delay(config.resultsDelay)
                    timeExpired = true
                }

                launch {
                    getResultsByAnyInput(availablePlugins, inputQuery)
                        .collect { results ->
                            accumulator += results

                            if (timeExpired) send(
                                operationResultSorter.invoke(
                                    inputQuery,
                                    accumulator
                                )
                            )
                        }
                }

                timerDeferrer.invokeOnCompletion {
                    launch {
                        send(operationResultSorter.invoke(inputQuery, accumulator))
                    }
                }
            }
        }

    private suspend fun getResultsByAnyInput(
        availablePlugins: List<Plugin>,
        inputQuery: String,
    ): Flow<List<OperationResult>> {
        return channelFlow {
            val anyInputPlugins = availablePlugins.filter { it.metadata.consumeAnyInput }

            anyInputPlugins.forEach { plugin ->
                val results = pluginRepository.getAnyResults(inputQuery, plugin)

                send(results)
            }
        }
    }
}

private fun PluginCommand.toOperationResult(): OperationResult {
    return CommandOperationResult(
        uuid = uuid,
        pluginUuid = pluginUuid,
        iconUri = iconUri,
        name = name,
    )
}
