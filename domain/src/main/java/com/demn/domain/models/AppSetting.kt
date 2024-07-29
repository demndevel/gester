package com.demn.domain.models

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