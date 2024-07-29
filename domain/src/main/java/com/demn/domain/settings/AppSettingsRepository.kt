package com.demn.domain.settings

import com.demn.domain.models.AppSettingMetadata
import java.util.UUID

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
}