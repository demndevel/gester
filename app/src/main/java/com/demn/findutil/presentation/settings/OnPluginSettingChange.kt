package com.demn.findutil.presentation.settings

import com.demn.domain.models.Plugin
import com.demn.plugincore.parcelables.PluginSetting

typealias OnPluginSettingChange = (plugin: Plugin, setting: PluginSetting, newValue: String) -> Unit
