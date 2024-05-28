package com.demn.fooplugin.services

import com.demn.plugincore.buildPluginMetadata

val barPluginMetadata = buildPluginMetadata("bar") {
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