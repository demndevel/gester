package com.demn.findutil.app_settings

import android.content.Context
import java.util.*

interface AppSettingsRepository {
    fun getAllSettingsMetadata(): List<AppSettingMetadata>

    fun setStringSetting(
        key: UUID,
        value: String
    )

    fun getStringSetting(key: UUID): String

    fun setNumerousSetting(
        key: UUID,
        value: Int
    )

    fun getNumerousSetting(key: UUID): Int

    fun setBooleanSetting(
        key: UUID,
        value: Boolean
    )

    fun getBooleanSetting(key: UUID): Boolean

    fun enablePlugin(pluginUuid: UUID)

    fun disablePlugin(pluginUuid: UUID)

    fun checkPluginEnabled(pluginUuid: UUID): Boolean
}

class MockAppSettingsRepositoryImpl : AppSettingsRepository {
    override fun getAllSettingsMetadata(): List<AppSettingMetadata> = emptyList()

    override fun setStringSetting(key: UUID, value: String) = Unit
    override fun getStringSetting(key: UUID): String = ""

    override fun setNumerousSetting(key: UUID, value: Int) = Unit
    override fun getNumerousSetting(key: UUID): Int = 123

    override fun setBooleanSetting(key: UUID, value: Boolean) = Unit
    override fun getBooleanSetting(key: UUID): Boolean = true

    override fun enablePlugin(pluginUuid: UUID) = Unit

    override fun disablePlugin(pluginUuid: UUID) = Unit

    override fun checkPluginEnabled(pluginUuid: UUID): Boolean = true
}

class AppSettingsRepositoryImpl(context: Context) : AppSettingsRepository {
    companion object {
        private const val SHARED_PREFERENCES_NAME = "appsettings"

        private val appSettings = listOf(
            AppSettingMetadata(
                key = UUID.fromString("29a8edfe-52bd-44c3-8d94-984d206fa24f"),
                title = "Compact mode",
                description = "Makes UI more compact",
                settingType = AppSettingType.Boolean
            ),
            AppSettingMetadata(
                key = UUID.fromString("c9c87cbe-7c80-4c32-b725-8a7a42d88581"),
                title = "How much digits do you know?",
                description = "Simple question about how much digits do you know",
                settingType = AppSettingType.Numerous
            ),
            AppSettingMetadata(
                key = UUID.fromString("2813ddb8-83a7-4a42-9db8-1df548622b46"),
                title = "Your name",
                description = "Your name or your deadname",
                settingType = AppSettingType.String
            ),
        )
    }

    private val sharedPreferences = context.getSharedPreferences(
        SHARED_PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    override fun getAllSettingsMetadata(): List<AppSettingMetadata> {
        return appSettings
    }

    override fun setStringSetting(key: UUID, value: String) {
        sharedPreferences.edit().apply {
            putString(key.toString(), value)

            commit()
        }
    }

    override fun getStringSetting(key: UUID): String {
        return sharedPreferences.getString(key.toString(), null) ?: ""
    }

    override fun setNumerousSetting(key: UUID, value: Int) {
        sharedPreferences.edit().apply {
            putInt(key.toString(), value)

            commit()
        }
    }

    override fun getNumerousSetting(key: UUID): Int {
        return sharedPreferences.getInt(key.toString(), 0)
    }

    override fun setBooleanSetting(key: UUID, value: Boolean) {
        sharedPreferences.edit().apply {
            putBoolean(key.toString(), value)

            commit()
        }
    }

    override fun getBooleanSetting(key: UUID): Boolean {
        return sharedPreferences.getBoolean(key.toString(), false)
    }

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