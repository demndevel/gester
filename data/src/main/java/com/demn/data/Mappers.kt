package com.demn.data

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
        pluginUuid,
        name,
        description
    )
}

fun PluginFallbackCommandCacheDbo.toPluginFallbackCommand(): PluginFallbackCommand {
    return PluginFallbackCommand(
        commandUuid,
        pluginUuid,
        name,
        description
    )
}

fun PluginWithCommandsDbo.toPluginCache(): PluginCache {
    return PluginCache(
        pluginMetadata = buildPluginMetadata(
            pluginUuid = pluginCacheDbo.pluginUuid,
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
            pluginMetadata.pluginUuid,
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
        pluginUuid = pluginUuid,
        name = name,
        description = description
    )
}

fun PluginFallbackCommand.toFallbackPluginCommandDbo(): PluginFallbackCommandCacheDbo {
    return PluginFallbackCommandCacheDbo(
        commandUuid = uuid,
        pluginUuid = pluginUuid,
        name = name,
        description = description
    )
}