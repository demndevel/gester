package com.demn.domain.models

import java.util.UUID

enum class PluginErrorType {
    Unloaded,
    Other
}

data class PluginError(
    val pluginUuid: UUID?,
    val pluginName: String?,
    val type: PluginErrorType,
    val message: String,
)
