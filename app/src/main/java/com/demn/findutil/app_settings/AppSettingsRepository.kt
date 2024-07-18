package com.demn.findutil.app_settings

import android.content.Context
import java.util.*

interface AppSettingsRepository {
    fun getAllSettings(): List<AppSetting>

    fun setStringSetting(
        key: UUID,
        value: String
    )

    fun setNumerousSetting(
        key: UUID,
        value: Int
    )

    fun setBooleanSetting(
        key: UUID,
        value: Boolean
    )

    fun enablePlugin(pluginUuid: UUID)

    fun disablePlugin(pluginUuid: UUID)

    fun checkPluginEnabled(pluginUuid: UUID): Boolean
}

class MockAppSettingsRepositoryImpl : AppSettingsRepository {
    override fun getAllSettings(): List<AppSetting> = emptyList()

    override fun setStringSetting(key: UUID, value: String) = Unit

    override fun setNumerousSetting(key: UUID, value: Int) = Unit

    override fun setBooleanSetting(key: UUID, value: Boolean) = Unit

    override fun enablePlugin(pluginUuid: UUID) = Unit

    override fun disablePlugin(pluginUuid: UUID) = Unit

    override fun checkPluginEnabled(pluginUuid: UUID): Boolean = true
}

class AppSettingsRepositoryImpl(context: Context) : AppSettingsRepository {
    companion object {
        private const val SHARED_PREFERENCES_NAME = "appsettings"

        private val appSettings = listOf(
            AppBooleanSetting(
                key = UUID.fromString("29a8edfe-52bd-44c3-8d94-984d206fa24f"),
                title = "Compact mode",
                description = "Makes UI more compact",
                value = false
            ),
            AppNumerousSetting(
                key = UUID.fromString("c9c87cbe-7c80-4c32-b725-8a7a42d88581"),
                title = "How much digits do you know?",
                description = "Simple question about how much digits do you know",
                value = 1
            ),
        )
    }

    private val sharedPreferences = context.getSharedPreferences(
        SHARED_PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    override fun getAllSettings(): List<AppSetting> {
        return appSettings
            .map { appSetting ->
                when (appSetting) {
                    is AppBooleanSetting -> {
                        val value = sharedPreferences.getBoolean(
                            appSetting.key.toString(),
                            false
                        )

                        return@map appSetting.copy(value = value)
                    }

                    is AppNumerousSetting -> {
                        val value = sharedPreferences.getInt(
                            appSetting.key.toString(),
                            0
                        )

                        return@map appSetting.copy(value = value)

                    }

                    is AppStringSetting -> {
                        val value = sharedPreferences.getString(
                            appSetting.key.toString(),
                            ""
                        )

                        return@map appSetting.copy(value = value ?: "")
                    }
                }
            }
    }

    override fun setStringSetting(key: UUID, value: String) {
        sharedPreferences.edit().apply {
            putString(key.toString(), value)

            commit()
        }
    }

    override fun setNumerousSetting(key: UUID, value: Int) {
        sharedPreferences.edit().apply {
            putInt(key.toString(), value)

            commit()
        }
    }

    override fun setBooleanSetting(key: UUID, value: Boolean) {
        sharedPreferences.edit().apply {
            putBoolean(key.toString(), value)

            commit()
        }
    }

    override fun enablePlugin(pluginUuid: UUID) {
        sharedPreferences.edit().apply {
            putBoolean(pluginUuid.toString(), true)
        }
    }

    override fun disablePlugin(pluginUuid: UUID) {
        sharedPreferences.edit().apply {
            putBoolean(pluginUuid.toString(), false)
        }
    }

    override fun checkPluginEnabled(pluginUuid: UUID): Boolean {
        return sharedPreferences.getBoolean(pluginUuid.toString(), true)
    }
}