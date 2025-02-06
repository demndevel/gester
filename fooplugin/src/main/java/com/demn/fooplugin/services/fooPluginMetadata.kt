package com.demn.fooplugin.services

import com.demn.plugincore.parcelables.PluginSummary
import com.demn.plugincore.parcelables.PluginVersion
import com.demn.plugincore.parcelables.buildPluginMetadata
import java.util.UUID

val fooPluginSummary = PluginSummary(
    pluginId = "foo",
    pluginVersion = PluginVersion(0, 0)
)

val fooPluginMetadata = buildPluginMetadata(
    pluginId = fooPluginSummary.pluginId,
    pluginName = "foo plugin"
) {
    description = "sample plugin with funny name 'foo'"
    consumeAnyInput = true
    version = fooPluginSummary.pluginVersion
}