package com.demn.fooplugin.services

import com.demn.plugincore.buildPluginMetadata
import java.util.UUID

val fooPluginMetadata = buildPluginMetadata(
    pluginUuid = UUID.fromString("83fabe54-1436-4402-be43-8e3765698efb"),
    pluginName = "foo plugin"
) {
    description = "sample plugin with funny name 'foo'"
    consumeAnyInput = true
    version = "0.1"
}