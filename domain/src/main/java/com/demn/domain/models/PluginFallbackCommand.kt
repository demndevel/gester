package com.demn.domain.models

import java.util.*

data class PluginFallbackCommand(
    val uuid: UUID,
    val pluginUuid: UUID,
    val name: String,
    val description: String? = null
)