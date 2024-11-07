package com.demn.findutil.app_settings

import android.content.Context
import com.demn.domain.settings.PluginAvailabilityRepository

class MockPluginAvailabilityRepository : PluginAvailabilityRepository {
    override fun enablePlugin(pluginId: String) = Unit

    override fun disablePlugin(pluginId: String) = Unit

    override fun checkPluginEnabled(pluginId: String): Boolean = true
}

class PluginAvailabilityRepositoryImpl(context: Context) : PluginAvailabilityRepository {
    companion object {
        private const val SHARED_PREFERENCES_NAME = "plugin_availability"
    }

    private val sharedPreferences = context.getSharedPreferences(
        SHARED_PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    override fun enablePlugin(pluginId: String) {
        sharedPreferences.edit().apply {
            putBoolean(pluginId, true)
            commit()
        }
    }

    override fun disablePlugin(pluginId: String) {
        sharedPreferences.edit().apply {
            putBoolean(pluginId, false)
            commit()
        }
    }

    override fun checkPluginEnabled(pluginId: String): Boolean {
        return sharedPreferences.getBoolean(pluginId, true)
    }
}