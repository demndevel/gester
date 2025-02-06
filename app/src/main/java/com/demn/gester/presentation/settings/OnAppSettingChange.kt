package com.demn.gester.presentation.settings

typealias OnAppSettingChange = (appSetting: com.demn.domain.models.AppSettingMetadata, newValue: String) -> Unit

typealias OnAppBooleanSettingChange = (appSetting: com.demn.domain.models.AppSettingMetadata, newValue: Boolean) -> Unit
