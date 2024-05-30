package com.demn.findutil.plugins

import com.demn.plugincore.PluginMetadata

sealed interface Plugin {
    val metadata: PluginMetadata
}

data class BuiltInPlugin internal constructor(
    override val metadata: PluginMetadata
): Plugin

data class ExternalPlugin internal constructor(
    override val metadata: PluginMetadata,
    internal val pluginService: PluginService
) : Plugin

internal data class PluginService(
    val packageName: String,
    val serviceName: String,
    val actions: List<String>,
    val categories: List<String>,
)