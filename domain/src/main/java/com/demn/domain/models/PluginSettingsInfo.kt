package com.demn.domain.models

import io.github.demndevel.gester.core.parcelables.PluginSetting

data class PluginSettingsInfo(
    val plugin: Plugin,
    val settings: List<PluginSetting>
)
