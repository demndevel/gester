package com.demn.findutil.presentation.settings

import com.demn.findutil.app_settings.AppSettingMetadata

typealias OnAppSettingChange = (appSetting: AppSettingMetadata, newValue: String) -> Unit

typealias OnAppBooleanSettingChange = (appSetting: AppSettingMetadata, newValue: Boolean) -> Unit
