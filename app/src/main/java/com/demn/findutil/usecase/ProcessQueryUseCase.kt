package com.demn.findutil.usecase

import com.demn.findutil.plugins.PluginRepository
import com.demn.plugincore.Plugin
import com.demn.plugincore.operation_result.OperationResult

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
        // TODO

        val resultsByAnyInput = plugins
            .filter { it.metadata.consumeAnyInput }
            .map { invokeAnyResults(it, inputQuery) }
            .flatten()

        return resultsByAnyInput
    }

    private suspend fun invokeAnyResults(plugin: Plugin, inputQuery: String): List<OperationResult> {
        return pluginRepository.getAnyResults(inputQuery, plugin)
    }
}