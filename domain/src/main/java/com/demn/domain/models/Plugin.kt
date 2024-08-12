package com.demn.domain.models

import com.demn.plugincore.parcelables.PluginMetadata

interface Plugin {
    val metadata: PluginMetadata
}