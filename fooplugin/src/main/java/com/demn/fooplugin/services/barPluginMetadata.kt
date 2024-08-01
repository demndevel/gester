package com.demn.fooplugin.services

import com.demn.plugincore.buildPluginMetadata
import java.util.UUID

internal val awesomeFallbackCommandUuid = UUID.fromString("f5b295c5-e21e-4335-bd59-4ccbf6c1ca51")

val barPluginMetadata = buildPluginMetadata(
    pluginUuid = UUID.fromString("922fb4da-48cd-482d-94d6-1766560e4aa8"),
    pluginName = "bar"
) {
    description = "some shitty description"
    consumeAnyInput = true
    version = "2.5"

    fallbackCommand(awesomeFallbackCommandUuid, "Search Google") {
        description = "Opens google search page with default browser app"
    }
}