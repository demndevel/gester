package com.demn.domain.models

import java.util.UUID

data class PluginCommand(
    val uuid: UUID,
    val pluginUuid: UUID,
    val name: String,
    val description: String?
)
