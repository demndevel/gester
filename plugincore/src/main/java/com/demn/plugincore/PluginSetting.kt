package com.demn.plugincore

import java.util.UUID

enum class PluginSettingType(val code: Int) {
    String(0),
    Number(1),
    Boolean(2)
}

data class PluginSetting(
    val pluginUuid: UUID,
    val settingKey: String,
    val settingValue: String,
    val settingType: PluginSettingType
)