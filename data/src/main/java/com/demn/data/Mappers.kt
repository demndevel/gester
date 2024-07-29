package com.demn.data

import com.demn.data.entities.PluginCacheDbo
import com.demn.data.entities.PluginCommandCacheDbo
import com.demn.data.entities.PluginWithCommandsDbo
import com.demn.data.entities.ResultFrecencyDbo
import com.demn.domain.data.PluginCache
import com.demn.domain.models.PluginCommand
import com.demn.domain.models.ResultFrecency
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
        pluginUuid = pluginCacheDbo.pluginUuid,
        name = pluginCacheDbo.pluginName,
        description = pluginCacheDbo.description,
        version = pluginCacheDbo.version,
        consumeAnyInput = pluginCacheDbo.consumeAnyInput,
        commands = commands.map { it.toPluginCommand(pluginCacheDbo.pluginUuid) }
    )
}

fun PluginCache.toPluginWithCommandsDbo(): PluginWithCommandsDbo {
    return PluginWithCommandsDbo(
        pluginCacheDbo = PluginCacheDbo(
            pluginUuid = pluginUuid,
            pluginName = name,
            description = description,
            version = version,
            consumeAnyInput = consumeAnyInput
        ),
        commands = commands.map(PluginCommand::toPluginCommandDbo)
    )
}

fun PluginCommand.toPluginCommandDbo(): PluginCommandCacheDbo {
    return PluginCommandCacheDbo(
        commandUuid = uuid,
        name = name,
        description = description
    )
}