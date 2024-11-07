package com.demn.plugincore.parcelables

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

enum class PluginSettingType {
    String,
    Number,
    Boolean
}

@Parcelize
data class PluginSetting(
    val pluginId: String,
    val pluginSettingUuid: UUID,
    val settingName: String,
    val settingDescription: String,
    val settingValue: String,
    val settingType: PluginSettingType
) : Parcelable