package com.demn.domain.pluginmanagement

import com.demn.domain.models.PluginSettingsInfo
import java.util.UUID

interface PluginSettingsRepository {
    suspend fun getAll(): List<PluginSettingsInfo>

    suspend fun set(pluginId: String, settingUuid: UUID, value: String)
}