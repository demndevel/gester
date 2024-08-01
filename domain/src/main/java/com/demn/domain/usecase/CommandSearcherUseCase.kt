package com.demn.domain.usecase

import com.demn.domain.models.PluginCommand
import com.demn.domain.plugin_management.PluginRepository
import com.frosch2010.fuzzywuzzy_kotlin.FuzzySearch

interface CommandSearcherUseCase {
    suspend operator fun invoke(input: String): List<PluginCommand>
}

class MockCommandSearcherUseCase : CommandSearcherUseCase {
    override suspend fun invoke(input: String): List<PluginCommand> = emptyList()
}

class CommandSearcherUseCaseImpl(
    private val pluginRepository: PluginRepository
) : CommandSearcherUseCase {
    override suspend fun invoke(input: String): List<PluginCommand> {
        val commands = pluginRepository.getAllCommands()

        return commands.filter { command ->
            val formattedCommandName = command.name
                .trimIndent()
                .replace(" ", "")
                .lowercase()
            val formattedInput = input
                .trimIndent()
                .replace(" ", "")
                .lowercase()

            val ratio = FuzzySearch.tokenSetPartialRatio(formattedCommandName, formattedInput)

            ratio >= 45
        }
    }
}