package com.demn.pluginloading

import com.demn.domain.data.PluginCache
import com.demn.domain.models.ExternalPlugin
import com.demn.domain.models.PluginCommand
import com.demn.domain.models.PluginFallbackCommand
import com.demn.plugincore.ParcelablePluginCommand
import com.demn.plugincore.ParcelablePluginFallbackCommand
import java.util.UUID

fun ParcelablePluginCommand.toPluginCommand(pluginUuid: UUID): PluginCommand = PluginCommand(
    uuid = this.uuid,
    pluginUuid = pluginUuid,
    name = this.name,
    description = this.description
)

fun ParcelablePluginFallbackCommand.toPluginFallbackCommand(pluginUuid: UUID): PluginFallbackCommand = PluginFallbackCommand(
    uuid = this.uuid,
    pluginUuid = pluginUuid,
    name = this.name,
    description = this.description
)

fun ExternalPlugin.toPluginCache(
    commands: List<PluginCommand>,
    fallbackCommands: List<PluginFallbackCommand>
): PluginCache {
    return PluginCache(
        pluginMetadata = metadata,
        commands = commands,
        fallbackCommands = fallbackCommands
    )
}