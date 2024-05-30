package com.demn.fooplugin.services

import com.demn.plugincore.buildPluginMetadata
import java.util.UUID

val barPluginMetadata = buildPluginMetadata(
    pluginUuid = UUID.fromString("83fabe54-1436-4402-be43-8e3765698efb"),
    pluginName = "bar"
) {
    description = "'bar' plugin; don't putat' with y9bar chat"
    consumeAnyInput = false
    version = "2.3"

    command("barfy", "todo4") {
        description = "meh some desc for barfy plugin"
    }

    command("barfel", "todo3") {
        description = "meow barfel plugin :("
    }
}