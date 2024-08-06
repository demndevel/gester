package com.demn.data

import com.demn.data.entities.PluginCacheDbo
import com.demn.data.entities.PluginCommandCacheDbo
import com.demn.data.entities.PluginWithCommandsDbo
import com.demn.data.entities.ResultFrecencyDbo
import com.demn.domain.data.PluginCache
import com.demn.domain.models.PluginCommand
import com.demn.domain.models.ResultFrecency
import com.demn.plugincore.PluginMetadata
import com.demn.plugincore.PluginVersion
import com.demn.plugincore.buildPluginMetadata
import java.util.UUID

fun ResultFrecencyDbo.toResultFrecency(): ResultFrecency {
    return ResultFrecency(
        resultHashCode = resultHashCode,
        input = input,
        usages = usages,
        recency = recency
    )
}

fun PluginCommandCacheDbo.toPluginCommand(pluginUuid: UUID): PluginCommand {
    return PluginCommand(
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
        commands = commands.map { it.toPluginCommand(pluginCacheDbo.pluginUuid) }
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
        commands = commands.map(PluginCommand::toPluginCommandDbo)
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