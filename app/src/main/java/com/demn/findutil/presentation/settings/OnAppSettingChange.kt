package com.demn.findutil.presentation.settings

import com.demn.domain.models.AppSettingMetadata

typealias OnAppSettingChange = (appSetting: com.demn.domain.models.AppSettingMetadata, newValue: String) -> Unit

typealias OnAppBooleanSettingChange = (appSetting: com.demn.domain.models.AppSettingMetadata, newValue: Boolean) -> Unit
