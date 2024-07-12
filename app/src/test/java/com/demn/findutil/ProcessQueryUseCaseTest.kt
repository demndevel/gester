package com.demn.findutil

import com.demn.findutil.plugins.PluginInvocationResult
import com.demn.findutil.plugins.PluginRepository
import com.demn.findutil.usecase.ProcessQueryUseCaseImpl
import com.demn.plugincore.Plugin
import com.demn.plugincore.PluginCommand
import com.demn.plugincore.PluginMetadata
import com.demn.plugincore.buildPluginMetadata
import com.demn.plugincore.operation_result.BasicOperationResult
import com.demn.plugincore.operation_result.OperationResult
import com.demn.plugincore.operation_result.TransitionOperationResult
import kotlinx.coroutines.runBlocking
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class ProcessQueryUseCaseTest {
    private val plugin1AnyInputResults = listOf(
        BasicOperationResult("meow 1"),
        BasicOperationResult("meow 1")
    )
    private val plugin1Uuid = UUID.randomUUID()
    private val plugin2Uuid = UUID.randomUUID()
    private val plugin2command1uuid = UUID.randomUUID()
    private val plugin2Command1Results = listOf(
        TransitionOperationResult(
            initialText = "init text command 1 plugin 2",
            finalText = "final text"
        )
    )
    private val pluginList = listOf(
        object : Plugin {
            override val metadata: PluginMetadata
                get() = buildPluginMetadata(
                    pluginUuid = plugin1Uuid,
                    pluginName = "plugin 1 name"
                ) {
                    consumeAnyInput = true
                }
        },
        object : Plugin {
            override val metadata: PluginMetadata
                get() = buildPluginMetadata(
                    pluginUuid = plugin2Uuid,
                    pluginName = "plugin 2 name"
                ) {
                    command(
                        uuid = plugin2command1uuid,
                        name = "command 1",
                        triggerRegex = "^meow"
                    )
                }
        }
    )

    private fun getProcessQueryUseCase(): ProcessQueryUseCaseImpl {
        return ProcessQueryUseCaseImpl(
            object : PluginRepository {
                override suspend fun getPluginMetadataList(): List<Plugin> {
                    return pluginList
                }

                override suspend fun invokePluginCommand(
                    input: String,
                    commandUuid: UUID
                ): PluginInvocationResult<List<OperationResult>> {
                    return PluginInvocationResult.Success(plugin2Command1Results)
                }

                override suspend fun getAnyResults(
                    input: String,
                    plugin: Plugin
                ): List<OperationResult> {
                    return plugin1AnyInputResults
                }

                override suspend fun getAllPluginCommands(): List<PluginCommand> {
                    return pluginList
                        .map {
                            it.metadata.commands
                        }
                        .flatten()
                }
            }
        )
    }

    @Test
    fun `invoke with any input`() {
        runBlocking {
            val useCase = getProcessQueryUseCase()
            val results = useCase.invoke(pluginList, "any input query")

            assertEquals(plugin1AnyInputResults, results)
        }
    }

    @Test
    fun `invoke with plugin command`() {
        runBlocking {
            val useCase = getProcessQueryUseCase()
            val results = useCase.invoke(pluginList, "meow sample query")

            assertEquals(plugin1AnyInputResults + plugin2Command1Results, results)
        }
    }
}