package com.demn.plugins

import android.content.Context
import java.util.*

interface CorePluginsSettingsRepository {
    fun set(settingUuid: UUID, value: String)

    fun getSetting(settingUuid: UUID): String
}

class CorePluginsSettingsRepositoryImpl(
    context: Context
) : CorePluginsSettingsRepository {
    companion object {
        const val CORE_PLUGINS_PREFERENCES_NAME = "CorePluginsPreferences"
    }

    private val preferences = context.getSharedPreferences(CORE_PLUGINS_PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun set(settingUuid: UUID, value: String) {
        preferences.edit().apply {
            putString(settingUuid.toString(), value)
            commit()
        }
    }

    override fun getSetting(settingUuid: UUID): String {
        return preferences.getString(settingUuid.toString(), null) ?: ""
    }
}