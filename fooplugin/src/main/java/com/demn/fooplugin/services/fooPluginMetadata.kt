package com.demn.fooplugin.services

import com.demn.plugincore.buildPluginMetadata

val fooPluginMetadata = buildPluginMetadata("foo plugin") {
    description = "sample plugin with funny name 'foo'"
    consumeAnyInput = true
    version = "0.1"

    command("foofy", "todo1") {
        description = "meh some description for foofy plugin"
    }

    command("foofel", "todo2") {
        description = "meow description for foofel plugin :) :)"
    }
}