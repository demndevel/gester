package com.demn.domain.models

import com.demn.plugincore.Plugin
import com.demn.plugincore.PluginMetadata

data class BuiltInPlugin(
    override val metadata: PluginMetadata
): Plugin