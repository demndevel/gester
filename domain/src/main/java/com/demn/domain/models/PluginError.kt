package com.demn.domain.models

enum class PluginErrorType {
    Unloaded,
    Other
}

data class PluginError(
    val pluginId: String?,
    val pluginName: String?,
    val type: PluginErrorType,
    val message: String,
)
