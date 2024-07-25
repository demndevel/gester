package com.demn.findutil

import android.content.Context
import android.content.Intent
import com.demn.plugincore.FindUtilPluginUuid
import com.demn.plugincore.PluginMetadata
import com.demn.plugincore.PluginSetting
import com.demn.plugincore.buildPluginMetadata
import com.demn.plugincore.operation_result.BasicOperationResult
import com.demn.plugincore.operation_result.OperationResult
import com.demn.plugins.CorePlugin
import java.util.UUID

private val openSettingsCommandUuid = UUID.fromString("0292e03b-ffa7-406c-a900-78b5f860bb81")
private val helpCommandUuid = UUID.fromString("ddb2ee6b-8a35-4b56-9278-0594a8ec7b9b")
private val commandsCommandUuid = UUID.fromString("59655e19-b299-4241-af6a-0ba19e4487c1")

private val findUtilPluginMetadata =
    buildPluginMetadata(
        FindUtilPluginUuid,
        "findutilplugin"
    ) {
        consumeAnyInput = false

        command(
            uuid = commandsCommandUuid,
            name = "commands",
            triggerRegex = "^/commands"
        ) {
            description = "displays list of all available commands over all plugins"
        }

        command(
            uuid = helpCommandUuid,
            name = "help",
            triggerRegex = "^/help"
        ) {
            description = "just help message"
        }

        command(
            uuid = openSettingsCommandUuid,
            name = "settings",
            triggerRegex = "^/settings"
        ) {
            description = "opens settings screen"
        }
    }

class FindUtilPlugin(
    private val context: Context
) : CorePlugin {
    override val metadata: PluginMetadata = findUtilPluginMetadata

    override fun getPluginSettings(): List<PluginSetting> {
        return emptyList()
    }

    override fun invokeAnyInput(input: String): List<OperationResult> {
        return emptyList()
    }

    override fun invokePluginCommand(input: String, uuid: UUID): List<OperationResult> {
        if (uuid == openSettingsCommandUuid) {
            val operationResult = BasicOperationResult(
                text = "settings",
                description = "open settings screen",
                intent = Intent(context, SettingsActivity::class.java)
            )

            return listOf(operationResult)
        }

        if (uuid == helpCommandUuid) {
            val operationResult = BasicOperationResult(
                text = "help",
                description = """help message about how FindUtil works…: 
                    - /help – help message
                    - /settings - opens settings screen
                    - /commands - shows the list of all available commands (like this)
                    
                    you can also do smth by doing smth
                    
                    another one peace of help message
                    
                    enjoy using this app!
                """.trimMargin()
            )

            return listOf(operationResult)
        }

        if (uuid == commandsCommandUuid) {
            val operationResult = BasicOperationResult(
                text = "commands",
                description = """list of all FindUtil commands available: 
                    - /help – help message
                    - /settings - opens settings screen
                    - /commands - shows the list of all available commands (like this)
                """.trimMargin()
            )

            return listOf(operationResult)
        }

        return emptyList()
    }

    override fun invokePluginFallbackCommand(input: String, uuid: UUID) = Unit
}