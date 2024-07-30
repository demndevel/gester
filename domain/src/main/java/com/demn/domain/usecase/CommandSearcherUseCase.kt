package com.demn.domain.usecase

import com.demn.domain.models.PluginCommand
import com.demn.domain.plugin_management.PluginRepository

interface CommandSearcherUseCase {
    suspend operator fun invoke(input: String): List<PluginCommand>
}

class MockCommandSearcherUseCase : CommandSearcherUseCase {
    override suspend fun invoke(input: String): List<PluginCommand> = emptyList()
}

class CommandSearcherUseCaseImpl(
    private val pluginRepository: PluginRepository
) : CommandSearcherUseCase {
    // TODO. This implementation is quick&dirty
    override suspend fun invoke(input: String): List<PluginCommand> {
        val commands = pluginRepository.getAllCommands()

         return commands.filter { command ->
            command.name.lowercase().contains(input.lowercase())
        }
    }
}