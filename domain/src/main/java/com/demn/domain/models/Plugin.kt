package com.demn.domain.models

import com.demn.plugincore.parcelables.PluginMetadata

data class Plugin(
    val metadata: PluginMetadata,
    val pluginService: PluginService
)

data class PluginService(
    val packageName: String,
    val serviceName: String,
    val actions: List<String>,
    val categories: List<String>,
)