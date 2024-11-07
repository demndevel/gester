package com.demn.plugincore.dsl

import android.os.ParcelUuid
import com.demn.plugincore.operationresult.OperationResult
import com.demn.plugincore.parcelables.ParcelablePluginCommand
import com.demn.plugincore.parcelables.ParcelablePluginFallbackCommand
import com.demn.plugincore.parcelables.PluginMetadata
import com.demn.plugincore.parcelables.PluginSetting

class PluginBuilderScope(val pluginMetadata: PluginMetadata) {
    var getPluginSettings: (() -> List<PluginSetting>)? = null
    var setSettingsHandler: ((settingUuid: ParcelUuid, newValue: String) -> Unit)? = null
    var getAllFallbackCommands: (() -> List<ParcelablePluginFallbackCommand>)? = null
    var getAllCommands: (() -> List<ParcelablePluginCommand>)? = null
    var executeCommandHandler: ((commandUuid: ParcelUuid) -> Unit)? = null
    var executeFallbackCommandHandler: ((commandUuid: ParcelUuid, input: String) -> Unit)? = null
    var executeAnyInputHandler: ((input: String) -> List<OperationResult>)? = null

    internal fun build(): PluginSetup {
        return PluginSetup(
            pluginMetadata = pluginMetadata,
            executeFallbackCommandHandler = executeFallbackCommandHandler ?: { _, _ -> },
            executeCommandHandler = executeCommandHandler ?: {},
            executeAnyInputHandler = executeAnyInputHandler ?: { emptyList() },
            getAllCommands = getAllCommands ?: { emptyList() },
            getAllFallbackCommands = getAllFallbackCommands ?: { emptyList() },
            setSettingsHandler = setSettingsHandler ?: { _, _ -> },
            getPluginSettings = getPluginSettings ?: { emptyList() },
        )
    }
}

fun setupPlugin(
    pluginMetadata: PluginMetadata,
    block: PluginBuilderScope.() -> Unit
): PluginSetup {
    return PluginBuilderScope(pluginMetadata)
        .apply(block)
        .build()
}