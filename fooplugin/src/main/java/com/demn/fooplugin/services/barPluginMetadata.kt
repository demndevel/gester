package com.demn.fooplugin.services

import com.demn.plugincore.buildPluginMetadata
import java.util.UUID

val barPluginMetadata = buildPluginMetadata(
    pluginUuid = UUID.fromString("922fb4da-48cd-482d-94d6-1766560e4aa8"),
    pluginName = "bar"
) {
    description = "'bar' plugin; don't putat' with y9bar chat"
    consumeAnyInput = false
    version = "2.3"

    command(UUID.randomUUID(), "todo4", "^bar") {
        description = "meh some desc for barfy plugin"
    }

    command(UUID.randomUUID(), "todo3", "^bar") {
        description = "meow barfel plugin :("
    }
}