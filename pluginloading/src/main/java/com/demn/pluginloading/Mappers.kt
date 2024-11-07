package com.demn.pluginloading

import com.demn.domain.data.PluginCache
import com.demn.domain.models.ExternalPlugin
import com.demn.domain.models.PluginCommand
import com.demn.domain.models.PluginFallbackCommand
import com.demn.plugincore.parcelables.ParcelablePluginCommand
import com.demn.plugincore.parcelables.ParcelablePluginFallbackCommand

fun ParcelablePluginCommand.toPluginCommand(pluginId: String): PluginCommand = PluginCommand(
    uuid = this.uuid,
    pluginId = pluginId,
    name = this.name,
    iconUri = this.iconUri,
    description = this.description
)

fun ParcelablePluginFallbackCommand.toPluginFallbackCommand(pluginId: String): PluginFallbackCommand =
    PluginFallbackCommand(
        uuid = this.uuid,
        pluginId = pluginId,
        name = this.name,
        iconUri = this.iconUri,
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