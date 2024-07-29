package com.demn.findutil.presentation.settings

import com.demn.domain.models.AppSettingMetadata
import com.demn.plugincore.Plugin
import com.demn.plugincore.PluginMetadata
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

    data class HasDataState(
        val pluginSettingsSections: List<PluginSettingsSection>,
        val appSettings: List<SettingField<com.demn.domain.models.AppSettingMetadata>>,
        val pluginAvailabilities: List<PluginAvailability>,
        override val saveButtonVisible: Boolean
    ) : SettingsScreenUiState {
        private val hasInvalidAppSettings = appSettings
            .any { it.validatedField is ValidatedField.Invalid }

        private val hasInvalidPluginSettings = pluginSettingsSections
            .map { it.settings }
            .flatten()
            .any { it.validatedField is ValidatedField.Invalid }

        val hasInvalidSettings = hasInvalidAppSettings || hasInvalidPluginSettings
    }
}

data class PluginSettingsSection(
    val plugin: Plugin,
    val settings: List<SettingField<PluginSetting>>
)

data class PluginAvailability(
    val pluginMetadata: PluginMetadata,
    val available: Boolean
)

data class SettingField<T>(
    val isEdited: Boolean,
    val settingMetadata: T,
    val validatedField: ValidatedStringField
)