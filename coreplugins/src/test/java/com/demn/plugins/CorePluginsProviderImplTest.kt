package com.demn.plugins

import com.demn.plugincore.PluginMetadata
import com.demn.plugincore.buildPluginMetadata
import com.demn.plugincore.operation_result.BasicOperationResult
import com.demn.plugincore.operation_result.OperationResult
import com.demn.plugincore.operation_result.TransitionOperationResult
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.test.Test

class CorePluginsProviderImplTest {
    private val plugin1AnyInputResults = listOf(
        BasicOperationResult("meow 1"),
        BasicOperationResult("meow 1")
    )
    private val plugin1Uuid = UUID.randomUUID()
    private val plugin2Uuid = UUID.randomUUID()
    private val plugin3Uuid = UUID.randomUUID()
    private val plugin2command1uuid = UUID.randomUUID()
    private val plugin3command2uuid = UUID.randomUUID()
    private val plugin2Command1Results = listOf(
        TransitionOperationResult(
            initialText = "init text command 1 plugin 2",
            finalText = "final text"
        )
    )
    private val plugin3Command2Results = listOf(
        TransitionOperationResult(
            initialText = "init text command 1 plugin 3",
            finalText = "final text"
        )
    )


    private val testCorePluginsProvider = CorePluginsProviderImpl(
        listOf(
            object : CorePlugin {
                override val metadata: PluginMetadata
                    get() = buildPluginMetadata(
                        pluginUuid = plugin1Uuid,
                        pluginName = "some plugin 1"
                    ) {
                        consumeAnyInput = true
                    }

                override fun invokeAnyInput(input: String): List<OperationResult> {
                    return plugin1AnyInputResults
                }

                override fun invokePluginCommand(input: String, uuid: UUID): List<OperationResult> {
                    return emptyList()
                }
            },
            object : CorePlugin {
                override val metadata: PluginMetadata
                    get() = buildPluginMetadata(
                        pluginUuid = plugin2Uuid,
                        pluginName = "some plugin 2"
                    ) {
                        consumeAnyInput = true

                        command(
                            plugin2command1uuid,
                            "command 1 plugin 2",
                            triggerRegex = "^meow"
                        ) {}
                    }

                override fun invokeAnyInput(input: String): List<OperationResult> {
                    return emptyList()
                }

                override fun invokePluginCommand(input: String, uuid: UUID): List<OperationResult> {
                    return if (plugin2command1uuid == uuid) plugin2Command1Results else emptyList()
                }
            },
            object : CorePlugin {
                override val metadata: PluginMetadata
                    get() = buildPluginMetadata(
                        pluginUuid = plugin3Uuid,
                        pluginName = "some plugin 3"
                    ) {
                        consumeAnyInput = false

                        command(
                            plugin3command2uuid,
                            "command 1 plugin 3",
                            triggerRegex = "^bark"
                        ) {}
                    }

                override fun invokeAnyInput(input: String): List<OperationResult> {
                    return emptyList()
                }

                override fun invokePluginCommand(input: String, uuid: UUID): List<OperationResult> {
                    return if (plugin3command2uuid == uuid) plugin3Command2Results else emptyList()
                }
            },
        )
    )

    @Test
    fun getPlugins() {
        val plugins = testCorePluginsProvider.getPlugins()

        assert(plugins.size == 3)
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Test
    fun invokeAnyInput() {
        GlobalScope.launch {
            val plugins = testCorePluginsProvider.getPlugins()

            val totalResults = plugins
                .map {
                    testCorePluginsProvider.invokeAnyInput("sample input", it.metadata.pluginUuid)
                }
                .flatten()

            assert(totalResults == plugin1AnyInputResults)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Test
    fun invokePluginCommand_1() {
        GlobalScope.launch {
            val results = testCorePluginsProvider.invokePluginCommand(
                input = "sample input",
                pluginCommandId = plugin2command1uuid,
                pluginUuid = plugin2Uuid
            )

            assert(results == plugin2Command1Results)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Test
    fun invokePluginCommand_2() {
        GlobalScope.launch {
            val results = testCorePluginsProvider.invokePluginCommand(
                input = "sample input",
                pluginCommandId = plugin3command2uuid,
                pluginUuid = plugin3Uuid
            )

            assert(results == plugin3Command2Results)
        }
    }
}