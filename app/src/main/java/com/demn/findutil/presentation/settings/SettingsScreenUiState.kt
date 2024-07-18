package com.demn.findutil.presentation.settings

import com.demn.findutil.app_settings.AppSetting
import com.demn.plugincore.Plugin
import com.demn.plugincore.PluginSetting

sealed interface SettingsScreenUiState {
    val saveButtonVisible: Boolean

    data object NoData : SettingsScreenUiState {
        override val saveButtonVisible: Boolean
            get() = false
    }

    data object Loading : SettingsScreenUiState {
        override val saveButtonVisible: Boolean
            get() = false
    }

    data class HasAppSettingsNoPluginSettings(
        val appSettingsSections: List<AppSettingsSection>,
        override val saveButtonVisible: Boolean
    ) : SettingsScreenUiState

    data class NoAppSettingsHasPluginSettings(
        val pluginSettingsSections: List<PluginSettingsSection>,
        override val saveButtonVisible: Boolean
    ) : SettingsScreenUiState

    data class HasAppSettingsHasPluginSettings(
        val pluginSettingsSections: List<PluginSettingsSection>,
        val appSettingsSections: List<AppSettingsSection>,
        override val saveButtonVisible: Boolean
    ) : SettingsScreenUiState
}

data class AppSettingsSection(
    val title: String,
    val settings: List<SettingField<AppSetting>>
)

data class PluginSettingsSection(
    val plugin: Plugin,
    val settings: List<SettingField<PluginSetting>>
)

data class SettingField<T>(val isEdited: Boolean, val validatedField: ValidatedField<T>)