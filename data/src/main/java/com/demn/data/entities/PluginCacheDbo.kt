package com.demn.data.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.UUID

@Entity
data class PluginCacheDbo(
    @PrimaryKey
    val pluginId: String,
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
        parentColumn = "pluginId",
        entityColumn = "pluginId"
    )
    val commands: List<PluginCommandCacheDbo>,

    @Relation(
        parentColumn = "pluginId",
        entityColumn = "pluginId"
    )
    val fallbackCommands: List<PluginFallbackCommandCacheDbo>
)

@Entity
data class PluginCommandCacheDbo(
    @PrimaryKey
    val commandUuid: UUID,
    val pluginId: String,
    val name: String,
    val iconUri: String,
    val description: String? = null
)

@Entity
data class PluginFallbackCommandCacheDbo(
    @PrimaryKey
    val commandUuid: UUID,
    val pluginId: String,
    val name: String,
    val iconUri: String,
    val description: String? = null
)