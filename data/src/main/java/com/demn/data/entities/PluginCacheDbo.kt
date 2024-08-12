package com.demn.data.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.UUID

@Entity
data class PluginCacheDbo(
    @PrimaryKey
    val pluginUuid: UUID,
    val pluginName: String,
    val description: String? = null,
    val versionMajor: Int,
    val versionMinor: Int,
    val consumeAnyInput: Boolean,
)

data class PluginWithCommandsDbo(
    @Embedded
    val pluginCacheDbo: PluginCacheDbo,

    @Relation(
        parentColumn = "pluginUuid",
        entityColumn = "pluginUuid"
    )
    val commands: List<PluginCommandCacheDbo>,

    @Relation(
        parentColumn = "pluginUuid",
        entityColumn = "pluginUuid"
    )
    val fallbackCommands: List<PluginFallbackCommandCacheDbo>
)

@Entity
data class PluginCommandCacheDbo(
    @PrimaryKey
    val commandUuid: UUID,
    val pluginUuid: UUID,
    val name: String,
    val iconUri: String,
    val description: String? = null
)

@Entity
data class PluginFallbackCommandCacheDbo(
    @PrimaryKey
    val commandUuid: UUID,
    val pluginUuid: UUID,
    val name: String,
    val iconUri: String,
    val description: String? = null
)