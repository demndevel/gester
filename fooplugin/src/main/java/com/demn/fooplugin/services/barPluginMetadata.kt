package com.demn.fooplugin.services

import com.demn.plugincore.parcelables.PluginSummary
import com.demn.plugincore.parcelables.PluginVersion
import com.demn.plugincore.parcelables.buildPluginMetadata
import java.util.UUID

internal val awesomeFallbackCommandUuid = UUID.fromString("f5b295c5-e21e-4335-bd59-4ccbf6c1ca51")

val barPluginSummary = PluginSummary(
    pluginId = "com.demn.fooplugin.funny.example",
    pluginVersion = PluginVersion(2, 5)
)

val barPluginMetadata = buildPluginMetadata(
    pluginId = barPluginSummary.pluginId,
    pluginName = "bar"
) {
    description = "some shitty description"
    consumeAnyInput = true
    version = barPluginSummary.pluginVersion
}