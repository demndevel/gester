package com.demn.findutil.plugins

import com.demn.plugincore.PluginMetadata

data class Plugin internal constructor(
    val id: Int,
    val metadata: PluginMetadata,
    internal val pluginService: PluginService
)

internal data class PluginService(
    val packageName: String,
    val serviceName: String,
    val actions: List<String>,
    val categories: List<String>,
)