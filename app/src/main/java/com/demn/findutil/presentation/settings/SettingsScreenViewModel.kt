package com.demn.findutil.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demn.findutil.app_settings.*
import com.demn.plugincore.*
import com.demn.pluginloading.PluginRepository
import com.demn.pluginloading.PluginSettingsRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

private data class SettingsScreenVmState(
    val isLoading: Boolean = false,
    val saveButtonVisible: Boolean = false,
    val pluginSettingsSections: List<PluginSettingsSection>? = null,
    val appSettingsSections: List<AppSettingsSection>? = null,
    val pluginAvailabilities: List<PluginAvailability>? = null
) {
    fun toUiState(): SettingsScreenUiState {
        if (isLoading) return SettingsScreenUiState.Loading

        if (pluginSettingsSections != null && appSettingsSections != null && pluginAvailabilities != null) {
            return SettingsScreenUiState.HasDataState(
                pluginSettingsSections = pluginSettingsSections,
                appSettingsSections = appSettingsSections,
                pluginAvailabilities = pluginAvailabilities,
                saveButtonVisible = saveButtonVisible,
            )
        }

        return SettingsScreenUiState.NoData
    }
}

class SettingsScreenViewModel(
    private val pluginSettingsRepository: PluginSettingsRepository,
    private val appSettingsRepository: AppSettingsRepository,
    private val pluginRepository: PluginRepository
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
            val pluginSettingsSections = pluginSettingsRepository
                .getAll()
                .map {
                    val validatableSettings =
                        it.settings.map { setting ->
                            SettingField(
                                isEdited = false,
                                ValidatedField.Valid(
                                    field = setting,
                                )
                            )
                        }

                    PluginSettingsSection(plugin = it.plugin, settings = validatableSettings)
                }

            val appSettingsSections = listOf( // todo
                AppSettingsSection(
                    title = "App settings",
                    settings = appSettingsRepository
                        .getAllSettings()
                        .map { appSetting ->
                            SettingField(
                                isEdited = false,
                                ValidatedField.Valid(
                                    field = appSetting
                                )
                            )
                        }
                )
            )

            val pluginAvailabilities = pluginRepository
                .getPluginList()
                .filter { it.metadata.pluginUuid != FindUtilPluginUuid }
                .map { plugin ->
                    PluginAvailability(
                        pluginMetadata = plugin.metadata,
                        available = appSettingsRepository.checkPluginEnabled(plugin.metadata.pluginUuid)
                    )
                }

            _state.update {
                it.copy(
                    isLoading = false,
                    pluginSettingsSections = pluginSettingsSections,
                    appSettingsSections = appSettingsSections,
                    pluginAvailabilities = pluginAvailabilities
                )
            }
        }
    }

    fun setPluginAvailability(metadata: PluginMetadata, available: Boolean) {
        when (available) {
            true -> appSettingsRepository.enablePlugin(metadata.pluginUuid)
            false -> appSettingsRepository.disablePlugin(metadata.pluginUuid)
        }

        _state.update {
            val mutableAvailabilities = (it.pluginAvailabilities ?: return@update it).toMutableList()
            val index = mutableAvailabilities
                .indexOfFirst { availability -> availability.pluginMetadata.pluginUuid == metadata.pluginUuid }

            mutableAvailabilities.set(
                index = index,
                element = PluginAvailability(
                    pluginMetadata = metadata,
                    available = available
                )
            )

            it.copy(
                pluginAvailabilities = mutableAvailabilities
            )
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

    fun setAppSetting(appSetting: AppSetting) {
        val validatedSetting = validateAppSetting(appSetting)
        updateValidatedAppSetting(validatedSetting)

        _state.update {
            it.copy(
                saveButtonVisible = true
            )
        }
    }

    fun save() {
        val pluginSettingsSections = _state.value.pluginSettingsSections ?: return
        val appSettingsSections = _state.value.appSettingsSections ?: return

        val allEditedPluginSettings = selectEditedPluginSettings(pluginSettingsSections)
        val allEditedAppSettings = selectEditedAppSettings(appSettingsSections)

        if (hasInvalidPluginSettings(allEditedPluginSettings) || hasInvalidAppSettings(allEditedAppSettings))
            return

        saveAllAppSettings(allEditedAppSettings)

        viewModelScope.launch {
            saveAllPluginSettings(allEditedPluginSettings)
        }

        _state.update {
            it.copy(
                saveButtonVisible = false
            )
        }
    }

    private fun saveAllAppSettings(appSettingsFields: List<SettingField<AppSetting>>) {
        for (field in appSettingsFields) {
            val appSetting = field.validatedField.field

            when (appSetting) {
                is AppStringSetting -> {
                    appSettingsRepository.setStringSetting(
                        key = appSetting.key,
                        value = appSetting.value
                    )
                }

                is AppBooleanSetting -> {
                    appSettingsRepository.setBooleanSetting(
                        key = appSetting.key,
                        value = appSetting.value
                    )
                }

                is AppNumerousSetting -> {
                    appSettingsRepository.setNumerousSetting(
                        key = appSetting.key,
                        value = appSetting.value
                    )
                }
            }
        }
    }

    private fun selectEditedPluginSettings(pluginSettingsSections: List<PluginSettingsSection>) =
        pluginSettingsSections
            .map { it.settings }
            .flatten()
            .filter { it.isEdited }

    private fun selectEditedAppSettings(appSettingsSections: List<AppSettingsSection>) =
        appSettingsSections
            .map { it.settings }
            .flatten()
            .filter { it.isEdited }

    private fun hasInvalidAppSettings(appSettings: List<SettingField<AppSetting>>) =
        appSettings.any { it.validatedField is ValidatedField.Invalid }

    private fun hasInvalidPluginSettings(pluginSettings: List<SettingField<PluginSetting>>) =
        pluginSettings.any { it.validatedField is ValidatedField.Invalid }

    private suspend fun saveAllPluginSettings(allPluginSettings: List<SettingField<PluginSetting>>) {
        val deferredList = allPluginSettings
            .map { settingField ->
                coroutineScope {
                    async {
                        val setting = settingField.validatedField.field

                        pluginSettingsRepository.set(
                            setting.pluginUuid,
                            setting.pluginSettingUuid,
                            setting.settingValue
                        )
                    }
                }
            }

        deferredList.awaitAll()
    }

    private fun updateValidatedPluginSetting(
        plugin: Plugin,
        setting: PluginSetting,
        validatedSetting: ValidatedField<PluginSetting>
    ) {
        val settingsSections = _state.value.pluginSettingsSections ?: return
        val sectionIndex = settingsSections.indexOfFirst { it.plugin == plugin }
        val newSettingsSections = settingsSections.toMutableList().apply {
            val pluginSettings = this[sectionIndex].settings.toMutableList().apply {
                val settingIndex = indexOfFirst { it.validatedField.field == setting }
                if (settingIndex != -1) this[settingIndex] = SettingField(true, validatedSetting)
            }

            this[sectionIndex] = this[sectionIndex].copy(settings = pluginSettings)
        }

        _state.update {
            it.copy(pluginSettingsSections = newSettingsSections)
        }
    }

    private fun updateValidatedAppSetting(
        validatedSetting: ValidatedField<AppSetting>,
    ) {
        val appSettingField = validatedSetting.field
        val settingsSections = _state.value.appSettingsSections ?: return
        val sectionIndex = settingsSections
            .indexOfFirst { section ->
                section.settings.any { setting -> setting.validatedField.field.key == appSettingField.key }
            }

        val newSettingsSections = settingsSections.toMutableList().apply {
            val appSettings = this[sectionIndex].settings.toMutableList().apply {
                val settingIndex = indexOfFirst { it.validatedField.field.key == appSettingField.key }
                if (settingIndex != -1) this[settingIndex] = SettingField(true, validatedSetting)
            }

            this[sectionIndex] = this[sectionIndex].copy(settings = appSettings)
        }

        _state.update {
            it.copy(
                appSettingsSections = newSettingsSections
            )
        }
    }

    private fun validateAppSetting(
        setting: AppSetting,
    ): ValidatedField<AppSetting> {
        if (setting is AppStringSetting) {
            if (setting.value.isBlank()) {
                return ValidatedField.Invalid(
                    field = setting,
                    error = SettingValidationError.ShouldNotBeBlank
                )
            }
        }

        return ValidatedField.Valid(
            field = setting
        )
    }

    private fun validatePluginSetting(
        setting: PluginSetting,
        newValue: String
    ): ValidatedField<PluginSetting> {
        when (setting.settingType) {
            PluginSettingType.Number -> {
                newValue.toIntOrNull() ?: return ValidatedField.Invalid(
                    field = setting.copy(
                        settingValue = newValue
                    ),
                    error = SettingValidationError.ShouldContainOnlyNumbers,
                )
            }

            PluginSettingType.Boolean -> {
                if (newValue != BooleanSettingTrue && newValue != BooleanSettingFalse) {
                    return ValidatedField.Invalid(
                        field = setting.copy(settingValue = newValue),
                        error = SettingValidationError.Other,
                    )
                }
            }

            PluginSettingType.String -> {
                if (newValue.isBlank()) {
                    return ValidatedField.Invalid(
                        field = setting.copy(settingValue = newValue),
                        error = SettingValidationError.ShouldNotBeBlank,
                    )
                }
            }
        }

        return ValidatedField.Valid(field = setting.copy(settingValue = newValue))
    }
}