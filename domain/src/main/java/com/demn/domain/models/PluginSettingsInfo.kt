package com.demn.domain.models

import com.demn.plugincore.parcelables.PluginSetting

data class PluginSettingsInfo(
    val plugin: Plugin,
    val settings: List<PluginSetting>
)