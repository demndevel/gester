package com.demn.plugins.core_plugins

import com.demn.plugincore.PluginMetadata
import com.demn.plugincore.buildPluginMetadata
import com.demn.plugincore.operation_result.BasicOperationResult
import com.demn.plugincore.operation_result.OperationResult
import com.demn.plugins.CorePlugin
import java.util.UUID

val appSearchingMetadata = buildPluginMetadata(
    pluginUuid = UUID.fromString("57198ae0-683a-4e2a-9db4-d229707b97ce"),
    pluginName = "App Searching Plugin"
) {
    description = "built-in plugin for plugin searching"
    version = "0.1"
    consumeAnyInput = true
}

class AppSearchingPlugin : CorePlugin {
    override val metadata: PluginMetadata = appSearchingMetadata

    override fun invokeAnyInput(input: String): List<OperationResult> {
        return listOf(
            BasicOperationResult(
                text = "Nekogram"
            ),
            BasicOperationResult(
                text = "Google Photo"
            ),
            BasicOperationResult(
                text = "GTA V"
            ),
            BasicOperationResult(
                text = "Amogus"
            )
        )
    }

    override fun invokePluginCommand(input: String, uuid: UUID): List<OperationResult> {
        return emptyList()
    }
}