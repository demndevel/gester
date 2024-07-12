package com.demn.findutil.presentation.settings

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

    data object HasAppSettingsNoPluginSettings : SettingsScreenUiState {
        override val saveButtonVisible: Boolean
            get() = false
    }

    data class NoAppSettingsHasPluginSettings(
        val pluginSettingsSections: List<SettingsSection>,
        override val saveButtonVisible: Boolean
    ) : SettingsScreenUiState

    data object HasAppSettingsHasPluginSettings : SettingsScreenUiState {
        override val saveButtonVisible: Boolean
            get() = false
    }
}

data class SettingsSection(
    val plugin: Plugin,
    val settings: List<SettingField<PluginSetting>>
)

data class SettingField<T>(val isEdited: Boolean, val validatedField: ValidatedField<T>)

sealed interface ValidatedField<T> {
    val value: T

    data class Valid<T>(
        override val value: T,
    ) : ValidatedField<T>

    data class InValid<T>(
        override val value: T,
        val error: SettingValidationError,
    ) : ValidatedField<T>
}

sealed interface SettingValidationError {
    data object ShouldContainOnlyNumbers : SettingValidationError

    data object ShouldNotBeBlank : SettingValidationError

    data object Other : SettingValidationError
}