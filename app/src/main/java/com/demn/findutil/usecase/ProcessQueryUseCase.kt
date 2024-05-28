package com.demn.findutil.usecase

import com.demn.findutil.plugins.Plugin
import com.demn.findutil.plugins.PluginRepository
import com.demn.plugincore.operation_result.BasicOperationResult
import com.demn.plugincore.operation_result.OperationResult
import kotlinx.coroutines.delay

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
        // TODO: here should be complex code to iterate through all plugins and process input query
        delay(200)

        plugins.forEach {
            pluginRepository.getAnyResults(inputQuery, it)
        }

        return listOf(
            BasicOperationResult(
                text = "Sample result",
                description = "Some example description of the sample result (basic operation result)."
            )
        )
    }
}