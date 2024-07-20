package com.demn.findutil.app_settings

import java.util.UUID

data class AppSettingMetadata(
    val key: UUID,
    val title: String,
    val description: String,
    val settingType: AppSettingType
)

enum class AppSettingType {
    String,
    Numerous,
    Boolean
}

//sealed interface AppSetting {
//    val key: UUID
//    val title: String
//    val description: String,
//
//}
//
//data class AppNumerousSetting(
//    val value: Int,
//    override val key: UUID,
//    override val title: String,
//    override val description: String
//) : AppSetting
//
//data class AppStringSetting(
//    val value: String,
//    override val key: UUID,
//    override val title: String,
//    override val description: String
//) : AppSetting
//
//data class AppBooleanSetting(
//    val value: Boolean,
//    override val key: UUID,
//    override val title: String,
//    override val description: String
//) : AppSetting