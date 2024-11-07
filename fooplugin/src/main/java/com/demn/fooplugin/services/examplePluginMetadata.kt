package com.demn.fooplugin.services

import android.net.Uri
import com.demn.plugincore.dsl.PluginService
import com.demn.plugincore.dsl.setupPlugin
import com.demn.plugincore.operationresult.IconOperationResult
import com.demn.plugincore.parcelables.PluginVersion
import com.demn.plugincore.parcelables.buildPluginMetadata
import java.util.UUID

val examplePluginMetadata = buildPluginMetadata(
    pluginId = "com.demn.fooplugin.funny.example",
    pluginName = "Example plugin"
) {
    version = PluginVersion(0, 0)

    description = "Some description idk"

    consumeAnyInput = true
}

val examplePluginSetup = setupPlugin(examplePluginMetadata) {
    executeAnyInputHandler = {
        listOf(
            IconOperationResult(
                iconUri = Uri.parse("android.resource://com.demn.fooplugin/drawable/save_icon"),
                text = "Some text for some result???",
                intent = null,
                label = "test",
                pinToTop = false
            )
        )
    }

    executeCommandHandler = { commandUuid ->
        // todo
    }

    executeFallbackCommandHandler = { commandUuid, input ->
        // todo
    }

    getAllCommands = {
        emptyList() // todo
    }

    setSettingsHandler = { settingUuid, newValue ->
        // todo
    }

    getPluginSettings = {
        emptyList() // todo
    }
}

class ExamplePluginLol : PluginService(examplePluginSetup)