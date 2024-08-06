package com.demn.pluginloading

import com.demn.domain.data.PluginCache
import com.demn.domain.models.ExternalPlugin
import com.demn.domain.models.PluginCommand
import com.demn.plugincore.ParcelablePluginCommand
import java.util.UUID

fun ParcelablePluginCommand.toPluginCommand(pluginUuid: UUID): PluginCommand = PluginCommand(
    uuid = this.uuid,
    pluginUuid = pluginUuid,
    name = this.name,
    description = this.description
)

fun ExternalPlugin.toPluginCache(commands: List<PluginCommand>): PluginCache {
    return PluginCache(
        pluginMetadata = metadata,
        commands = commands
    )
}