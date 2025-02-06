package com.demn.gester.presentation.settings

import com.demn.domain.models.Plugin
import io.github.demndevel.gester.core.parcelables.PluginSetting

typealias OnPluginSettingChange = (plugin: Plugin, setting: PluginSetting, newValue: String) -> Unit
