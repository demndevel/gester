package com.demn.findutil.preferences

import java.util.UUID

sealed interface AppSetting {
    val key: UUID
    val title: String
    val description: String
}

data class AppNumerousSetting(
    val value: Int,
    override val key: UUID,
    override val title: String,
    override val description: String
) : AppSetting

data class AppStringSetting(
    val value: String,
    override val key: UUID,
    override val title: String,
    override val description: String
) : AppSetting

data class AppBooleanSetting(
    val value: Boolean,
    override val key: UUID,
    override val title: String,
    override val description: String
) : AppSetting