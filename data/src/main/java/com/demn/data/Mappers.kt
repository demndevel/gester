package com.demn.data

import android.net.Uri
import com.demn.data.entities.*
import com.demn.domain.data.PluginCache
import com.demn.domain.models.PluginCommand
import com.demn.domain.models.PluginFallbackCommand
import com.demn.domain.models.ResultFrecency
import com.demn.plugincore.parcelables.PluginVersion
import com.demn.plugincore.parcelables.buildPluginMetadata

fun ResultFrecencyDbo.toResultFrecency(): ResultFrecency {
    return ResultFrecency(
        resultHashCode = resultHashCode,
        input = input,
        usages = usages,
        recency = recency
    )
}

fun PluginCommandCacheDbo.toPluginCommand(): PluginCommand {
    return PluginCommand(
        commandUuid,
        pluginId,
        Uri.parse(iconUri),
        name,
        description
    )
}

fun PluginFallbackCommandCacheDbo.toPluginFallbackCommand(): PluginFallbackCommand {
    return PluginFallbackCommand(
        uuid = commandUuid,
        pluginId = pluginId,
        name = name,
        iconUri = Uri.parse(iconUri),
        description = description
    )
}

fun PluginWithCommandsDbo.toPluginCache(): PluginCache {
    return PluginCache(
        pluginMetadata = buildPluginMetadata(
            pluginId = pluginCacheDbo.pluginId,
            pluginName = pluginCacheDbo.pluginName
        ) {
            description = pluginCacheDbo.description
            version = PluginVersion(pluginCacheDbo.versionMajor, pluginCacheDbo.versionMinor)
            consumeAnyInput = pluginCacheDbo.consumeAnyInput
        },
        commands = commands.map { it.toPluginCommand() },
        fallbackCommands = fallbackCommands.map { it.toPluginFallbackCommand() }
    )
}

fun PluginCache.toPluginWithCommandsDbo(): PluginWithCommandsDbo {
    return PluginWithCommandsDbo(
        pluginCacheDbo = PluginCacheDbo(
            pluginMetadata.pluginId,
            pluginMetadata.pluginName,
            pluginMetadata.description,
            pluginMetadata.version.major,
            pluginMetadata.version.minor,
            pluginMetadata.consumeAnyInput
        ),
        commands = commands.map(PluginCommand::toPluginCommandDbo),
        fallbackCommands = fallbackCommands.map(PluginFallbackCommand::toFallbackPluginCommandDbo)
    )
}

fun PluginCommand.toPluginCommandDbo(): PluginCommandCacheDbo {
    return PluginCommandCacheDbo(
        commandUuid = uuid,
        pluginId = pluginId,
        name = name,
        description = description,
        iconUri = iconUri.toString(),
    )
}

fun PluginFallbackCommand.toFallbackPluginCommandDbo(): PluginFallbackCommandCacheDbo {
    return PluginFallbackCommandCacheDbo(
        commandUuid = uuid,
        pluginId = pluginId,
        name = name,
        iconUri = iconUri.toString(),
        description = description
    )
}