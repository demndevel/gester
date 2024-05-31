package com.demn.plugins

import com.demn.plugincore.Plugin
import com.demn.plugincore.PluginMetadata

data class BuiltInPlugin internal constructor(
    override val metadata: PluginMetadata
): Plugin