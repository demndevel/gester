package com.demn.findutil

import android.content.Context
import android.content.Intent
import com.demn.domain.models.PluginCommand
import com.demn.domain.models.PluginFallbackCommand
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
    }

class FindUtilPlugin(
    private val context: Context
) : CorePlugin {
    override val metadata: PluginMetadata = findUtilPluginMetadata

    override fun invokeCommand(uuid: UUID) {
        if (uuid == openSettingsCommandUuid) {
            val intent = Intent(context, SettingsActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            context.startActivity(intent)
        }
    }

    override fun getPluginCommands(): List<PluginCommand> =
        listOf(
            PluginCommand(
                uuid = openSettingsCommandUuid,
                pluginUuid = metadata.pluginUuid,
                name = "FindUtil settings",
                description = null
            )
        )

    override fun getPluginFallbackCommands(): List<PluginFallbackCommand> = emptyList()

    override fun getPluginSettings(): List<PluginSetting> {
        return emptyList()
    }

    override fun invokeAnyInput(input: String): List<OperationResult> {
        return emptyList()
    }

    override fun invokePluginFallbackCommand(input: String, uuid: UUID) = Unit
}