package com.demn.domain.models

import com.demn.plugincore.parcelables.PluginMetadata

data class BuiltInPlugin(
    override val metadata: PluginMetadata
): Plugin