package com.demn.findutil.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demn.findutil.plugins.PluginSettingsRepository
import com.demn.plugincore.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private data class SettingsScreenVmState(
    val isLoading: Boolean = false,
    val saveButtonVisible: Boolean = false,
    val settingsSections: List<SettingsSection>? = null
) {
    fun toUiState(): SettingsScreenUiState {
        if (isLoading) return SettingsScreenUiState.Loading
        if (settingsSections == null) return SettingsScreenUiState.NoData

        return SettingsScreenUiState.NoAppSettingsHasPluginSettings(
            pluginSettingsSections = settingsSections,
            saveButtonVisible = saveButtonVisible
        )
    }
}

class SettingsScreenViewModel(
    private val pluginSettingsRepository: PluginSettingsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsScreenVmState())

    val state: StateFlow<SettingsScreenUiState> = _state
        .map(SettingsScreenVmState::toUiState)
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            SettingsScreenUiState.NoData,
        )

    fun loadData() {
        viewModelScope.launch {
            val settingsSections = pluginSettingsRepository
                .getAll()
                .map {
                    val validatableSettings =
                        it.settings.map { setting ->
                            SettingField(
                                isEdited = false,
                                ValidatedField.Valid(
                                    value = setting,
                                )
                            )
                        }

                    SettingsSection(plugin = it.plugin, settings = validatableSettings)
                }

            _state.update {
                it.copy(
                    isLoading = false,
                    settingsSections = settingsSections
                )
            }
        }
    }

    fun setPluginSetting(plugin: Plugin, setting: PluginSetting, newValue: String) {
        val validatedSetting = validatePluginSetting(setting, newValue)

        updateValidatedPluginSetting(plugin, setting, validatedSetting)

        _state.update {
            it.copy(
                saveButtonVisible = true
            )
        }
    }

    fun save() {
        val settingsSections = _state.value.settingsSections ?: return

        val allPluginSettings = settingsSections
            .map { it.settings }
            .flatten()
            .filter { it.isEdited }

        val isThereAnyInvalidSetting = allPluginSettings.any { it.validatedField is ValidatedField.InValid }

        if (isThereAnyInvalidSetting) return

        viewModelScope.launch {
            val deferredList = allPluginSettings
                .map { settingField ->
                    async {
                        val setting = settingField.validatedField.value

                        pluginSettingsRepository.set(
                            setting.pluginUuid,
                            setting.pluginSettingUuid,
                            setting.settingValue
                        )
                    }
                }

            deferredList.awaitAll()
        }

        _state.update {
            it.copy(
                saveButtonVisible = false
            )
        }
    }

    private fun updateValidatedPluginSetting(
        plugin: Plugin,
        setting: PluginSetting,
        validatedSetting: ValidatedField<PluginSetting>
    ) {
        val settingsSections = _state.value.settingsSections ?: return
        val pluginIndex = settingsSections.indexOfFirst { it.plugin == plugin }
        val newSettingsSections = settingsSections.toMutableList().apply {
            val pluginSettings = this[pluginIndex].settings.toMutableList().apply {
                val settingIndex = indexOfFirst { it.validatedField.value == setting }
                if (settingIndex != -1) this[settingIndex] = SettingField(true, validatedSetting)
            }
            this[pluginIndex] = this[pluginIndex].copy(settings = pluginSettings)
        }

        _state.update {
            it.copy(settingsSections = newSettingsSections)
        }
    }

    private fun validatePluginSetting(
        setting: PluginSetting,
        newValue: String
    ): ValidatedField<PluginSetting> {
        when (setting.settingType) {
            PluginSettingType.Number -> {
                newValue.toIntOrNull() ?: return ValidatedField.InValid(
                    value = setting.copy(
                        settingValue = newValue
                    ),
                    error = SettingValidationError.ShouldContainOnlyNumbers,
                )
            }

            PluginSettingType.Boolean -> {
                if (newValue == BooleanSettingTrue || newValue == BooleanSettingFalse) {
                    return ValidatedField.InValid(
                        value = setting.copy(settingValue = newValue),
                        error = SettingValidationError.Other,
                    )
                }
            }

            PluginSettingType.String -> {
                if (newValue.isBlank()) {
                    return ValidatedField.InValid(
                        value = setting.copy(settingValue = newValue),
                        error = SettingValidationError.ShouldNotBeBlank,
                    )
                }
            }
        }

        return ValidatedField.Valid(value = setting.copy(settingValue = newValue))
    }
}