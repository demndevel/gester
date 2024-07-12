package com.demn.pluginloading

import com.demn.plugincore.Plugin
import com.demn.plugincore.PluginMetadata

data class ExternalPlugin internal constructor(
    override val metadata: PluginMetadata,
    internal val pluginService: PluginService
) : Plugin

data class PluginService(
    val packageName: String,
    val serviceName: String,
    val actions: List<String>,
    val categories: List<String>,
)