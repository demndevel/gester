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
    val version: String,
    val consumeAnyInput: Boolean,
)

data class PluginWithCommandsDbo(
    @Embedded
    val pluginCacheDbo: PluginCacheDbo,

    @Relation(
        parentColumn = "pluginUuid",
        entityColumn = "commandUuid"
    )
    val commands: List<PluginCommandCacheDbo>
)

@Entity
data class PluginCommandCacheDbo(
    @PrimaryKey
    val commandUuid: UUID,
    val name: String,
    val description: String? = null
)
