package com.demn.fooplugin.services

import com.demn.plugincore.parcelables.PluginSummary
import com.demn.plugincore.parcelables.PluginVersion
import com.demn.plugincore.parcelables.buildPluginMetadata
import java.util.UUID

val fooPluginSummary = PluginSummary(
    UUID.fromString("83fabe54-1436-4402-be43-8e3765698efb"),
    PluginVersion(0, 0)
)

val fooPluginMetadata = buildPluginMetadata(
    pluginUuid = fooPluginSummary.pluginUuid,
    pluginName = "foo plugin"
) {
    description = "sample plugin with funny name 'foo'"
    consumeAnyInput = true
    version = fooPluginSummary.pluginVersion
}