package com.demn.domain.plugin_management

import com.demn.domain.models.PluginSettingsInfo
import java.util.UUID

interface PluginSettingsRepository {
    suspend fun getAll(): List<PluginSettingsInfo>

    suspend fun set(pluginUuid: UUID, settingUuid: UUID, value: String)
}