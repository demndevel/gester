package com.demn.findutil

import android.content.Context
import android.content.Intent
import com.demn.plugincore.PluginMetadata
import com.demn.plugincore.buildPluginMetadata
import com.demn.plugincore.operation_result.BasicOperationResult
import com.demn.plugincore.operation_result.OperationResult
import com.demn.plugins.CorePlugin
import java.util.UUID

private val openSettingsCommandUuid = UUID.fromString("0292e03b-ffa7-406c-a900-78b5f860bb81")
private val findUtilPluginMetadata =
    buildPluginMetadata(
        UUID.fromString("6f0d8823-648b-42cb-918a-053c612f1391"),
        "findutilplugin"
    ) {
        consumeAnyInput = false

        command( // todo
            uuid = UUID.fromString("d3b328df-09e3-4260-b5b7-26f1149d8c6d"),
            name = "commands",
            triggerRegex = "^/commands"
        ) {
            description = "displays list of all available commands over all plugins"
        }

        command( // todo
            uuid = UUID.fromString("6f361e0d-e2c2-49b7-84a2-7271180f939f"),
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

        return emptyList()
    }
}