package com.demn.findutil.presentation.settings

import com.demn.plugincore.Plugin
import com.demn.plugincore.PluginSetting

typealias OnPluginSettingChange = (plugin: Plugin, setting: PluginSetting, newValue: String) -> Unit
