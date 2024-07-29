package com.demn.findutil.app_settings

import android.content.Context
import com.demn.domain.settings.PluginAvailabilityRepository
import java.util.UUID

class MockPluginAvailabilityRepository : PluginAvailabilityRepository {
    override fun enablePlugin(pluginUuid: UUID) = Unit

    override fun disablePlugin(pluginUuid: UUID) = Unit

    override fun checkPluginEnabled(pluginUuid: UUID): Boolean = true
}

class PluginAvailabilityRepositoryImpl(context: Context) : PluginAvailabilityRepository {
    companion object {
        private const val SHARED_PREFERENCES_NAME = "plugin_availability"
    }

    private val sharedPreferences = context.getSharedPreferences(
        SHARED_PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    override fun enablePlugin(pluginUuid: UUID) {
        sharedPreferences.edit().apply {
            putBoolean(pluginUuid.toString(), true)
            commit()
        }
    }

    override fun disablePlugin(pluginUuid: UUID) {
        sharedPreferences.edit().apply {
            putBoolean(pluginUuid.toString(), false)
            commit()
        }
    }

    override fun checkPluginEnabled(pluginUuid: UUID): Boolean {
        return sharedPreferences.getBoolean(pluginUuid.toString(), true)
    }
}