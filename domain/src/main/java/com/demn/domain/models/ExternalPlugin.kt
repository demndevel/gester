package com.demn.domain.models

import com.demn.plugincore.parcelables.PluginMetadata

data class ExternalPlugin(
    override val metadata: PluginMetadata,
    val pluginService: PluginService
) : Plugin

data class PluginService(
    val packageName: String,
    val serviceName: String,
    val actions: List<String>,
    val categories: List<String>,
)