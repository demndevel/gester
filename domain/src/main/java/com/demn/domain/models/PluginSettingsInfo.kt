package com.demn.domain.models

import com.demn.plugincore.Plugin
import com.demn.plugincore.PluginSetting

data class PluginSettingsInfo(
    val plugin: Plugin,
    val settings: List<PluginSetting>
)