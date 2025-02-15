package com.demn.gester.presentation.settings

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demn.domain.models.AppSettingMetadata
import com.demn.domain.models.AppSettingType
import com.demn.domain.models.Plugin
import com.demn.domain.pluginmanagement.PluginRepository
import com.demn.domain.pluginmanagement.PluginSettingsRepository
import com.demn.domain.pluginmanagement.PluginUninstaller
import com.demn.domain.settings.AppSettingsRepository
import com.demn.domain.settings.PluginAvailabilityRepository
import com.demn.domain.usecase.PluginCacheSyncUseCase
import com.demn.gester.gesterPluginId
import io.github.demndevel.gester.core.BooleanSettingFalse
import io.github.demndevel.gester.core.BooleanSettingTrue
import io.github.demndevel.gester.core.parcelables.PluginMetadata
import io.github.demndevel.gester.core.parcelables.PluginSetting
import io.github.demndevel.gester.core.parcelables.PluginSettingType
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@Immutable
private data class SettingsScreenVmState(
    val isLoading: Boolean = false,
    val pluginSettingsSections: List<PluginSettingsSection>? = null,
    val appSettings: List<SettingField<AppSettingMetadata>>? = null,
    val pluginAvailabilities: List<PluginAvailability>? = null,
    val saveButtonVisible: Boolean = false
) {
    fun toUiState(): SettingsScreenUiState {
        if (isLoading) return SettingsScreenUiState.Loading

        if (pluginSettingsSections != null && appSettings != null && pluginAvailabilities != null) {
            return SettingsScreenUiState.HasDataState(
                pluginSettingsSections = pluginSettingsSections,
                appSettings = appSettings,
                pluginAvailabilities = pluginAvailabilities,
                saveButtonVisible = saveButtonVisible
            )
        }

        return SettingsScreenUiState.NoData
    }
}

class SettingsScreenViewModel(
    private val pluginSettingsRepository: PluginSettingsRepository,
    private val appSettingsRepository: AppSettingsRepository,
    private val pluginRepository: PluginRepository,
    private val pluginAvailabilityRepository: PluginAvailabilityRepository,
    private val pluginUninstaller: PluginUninstaller,
    private val pluginCacheSyncUseCase: PluginCacheSyncUseCase,
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
                                settingMetadata = setting,
                                ValidatedField.Valid(setting.settingValue)
                            )
                        }

                    PluginSettingsSection(plugin = it.plugin, settings = validatableSettings)
                }

            val appSettings = appSettingsRepository
                .getAllSettingsMetadata()
                .map { appSetting ->
                    val settingValue = when (appSetting.settingType) {
                        AppSettingType.String -> appSettingsRepository.getStringSetting(
                            appSetting.key
                        )

                        AppSettingType.Numerous -> appSettingsRepository.getNumerousSetting(
                            appSetting.key
                        ).toString()

                        AppSettingType.Boolean -> appSettingsRepository.getBooleanSetting(
                            appSetting.key
                        ).toString()
                    }

                    SettingField(
                        isEdited = false,
                        settingMetadata = appSetting,
                        ValidatedField.Valid(settingValue)
                    )
                }

            val pluginAvailabilities = pluginRepository
                .getPluginList().plugins
                .filter { it.metadata.pluginId != gesterPluginId }
                .map { plugin ->
                    PluginAvailability(
                        pluginMetadata = plugin.metadata,
                        available = pluginAvailabilityRepository.checkPluginEnabled(plugin.metadata.pluginId)
                    )
                }

            _state.update {
                it.copy(
                    isLoading = false,
                    pluginSettingsSections = pluginSettingsSections,
                    appSettings = appSettings,
                    pluginAvailabilities = pluginAvailabilities
                )
            }
        }
    }

    fun setPluginAvailability(metadata: PluginMetadata, available: Boolean) {
        when (available) {
            true -> pluginAvailabilityRepository.enablePlugin(metadata.pluginId)
            false -> pluginAvailabilityRepository.disablePlugin(metadata.pluginId)
        }

        _state.update {
            val mutableAvailabilities =
                (it.pluginAvailabilities ?: return@update it).toMutableList()
            val index = mutableAvailabilities
                .indexOfFirst { availability -> availability.pluginMetadata.pluginId == metadata.pluginId }

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

    fun setAppSetting(
        appSettingMetadata: AppSettingMetadata,
        newValue: String
    ) {
        val validatedSetting = validateAppSetting(appSettingMetadata, newValue)
        updateValidatedAppSetting(appSettingMetadata, validatedSetting)

        _state.update {
            it.copy(
                saveButtonVisible = true
            )
        }
    }

    fun setBooleanAppSetting(
        appSettingMetadata: AppSettingMetadata,
        newValue: Boolean
    ) {
        val validatedSetting = ValidatedField.Valid(newValue.toString())
        updateValidatedAppSetting(appSettingMetadata, validatedSetting)

        _state.update {
            it.copy(
                saveButtonVisible = true
            )
        }
    }

    fun save() {
        val pluginSettingsSections = _state.value.pluginSettingsSections ?: return
        val appSettings = _state.value.appSettings ?: return

        val allEditedPluginSettings = selectEditedPluginSettings(pluginSettingsSections)

        val allEditedAppSettings = selectEditedAppSettings(appSettings)

        if (hasInvalidPluginSettings(allEditedPluginSettings) || hasInvalidAppSettings(
                allEditedAppSettings
            )
        ) {
            return
        }

        saveAllAppSettings(allEditedAppSettings)

        viewModelScope.launch {
            saveAllPluginSettings(allEditedPluginSettings)

            _state.update {
                it.copy(
                    saveButtonVisible = false
                )
            }

            loadData()
        }
    }

    private fun saveAllAppSettings(appSettingsFields: List<SettingField<AppSettingMetadata>>) {
        for (field in appSettingsFields) {
            val appSettingMetadata = field.settingMetadata

            when (appSettingMetadata.settingType) {
                AppSettingType.String -> {
                    appSettingsRepository.setStringSetting(
                        key = appSettingMetadata.key,
                        value = field.validatedField.field
                    )
                }

                AppSettingType.Boolean -> {
                    appSettingsRepository.setBooleanSetting(
                        key = appSettingMetadata.key,
                        value = field.validatedField.field.toBoolean()
                    )
                }

                AppSettingType.Numerous -> {
                    appSettingsRepository.setNumerousSetting(
                        key = appSettingMetadata.key,
                        value = field.validatedField.field.toInt()
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

    private fun selectEditedAppSettings(appSettingsSections: List<SettingField<AppSettingMetadata>>) =
        appSettingsSections
            .filter { it.isEdited }

    private fun hasInvalidAppSettings(appSettings: List<SettingField<AppSettingMetadata>>) =
        appSettings.any { it.validatedField is ValidatedField.Invalid }

    private fun hasInvalidPluginSettings(pluginSettings: List<SettingField<PluginSetting>>) =
        pluginSettings.any { it.validatedField is ValidatedField.Invalid }

    private suspend fun saveAllPluginSettings(allPluginSettings: List<SettingField<PluginSetting>>) {
        val deferredList =
            allPluginSettings
                .map { settingField ->
                    viewModelScope.async {
                        val value = settingField.validatedField.field

                        pluginSettingsRepository.set(
                            settingField.settingMetadata.pluginId,
                            settingField.settingMetadata.pluginSettingUuid,
                            value
                        )
                    }
                }

        deferredList.awaitAll()
    }

    private fun updateValidatedPluginSetting(
        plugin: Plugin,
        setting: PluginSetting,
        validatedSetting: ValidatedStringField
    ) {
        val settingsSections = _state.value.pluginSettingsSections ?: return
        val sectionIndex = settingsSections.indexOfFirst { it.plugin == plugin }
        val newSettingsSections = settingsSections.toMutableList().apply {
            val pluginSettings = this[sectionIndex].settings.toMutableList().apply {
                val settingIndex = indexOfFirst { it.settingMetadata == setting }
                if (settingIndex != -1) this[settingIndex] =
                    SettingField(
                        isEdited = true,
                        settingMetadata = setting,
                        validatedField = validatedSetting
                    )
            }

            this[sectionIndex] = this[sectionIndex].copy(settings = pluginSettings)
        }

        _state.update {
            it.copy(pluginSettingsSections = newSettingsSections)
        }
    }

    private fun updateValidatedAppSetting(
        appSettingMetadata: AppSettingMetadata,
        newValidatedValue: ValidatedStringField
    ) {
        val appSettings = _state.value.appSettings ?: return

        val newSettings = appSettings
            .map {
                if (it.settingMetadata.key == appSettingMetadata.key) {
                    it.copy(
                        isEdited = true,
                        validatedField = newValidatedValue
                    )
                } else it
            }

        _state.update {
            it.copy(
                appSettings = newSettings
            )
        }
    }

    private fun validateAppSetting(
        metadata: AppSettingMetadata,
        newValue: String
    ): ValidatedField<String> {
        if (metadata.settingType == AppSettingType.String) {
            if (newValue.isBlank()) {
                return ValidatedField.Invalid(
                    field = newValue,
                    error = SettingValidationError.ShouldNotBeBlank
                )
            }
        }

        if (metadata.settingType == AppSettingType.Numerous) {
            if (newValue.toIntOrNull() == null) {
                return ValidatedField.Invalid(
                    field = newValue,
                    error = SettingValidationError.ShouldContainOnlyNumbers
                )
            }
        }

        return ValidatedField.Valid(
            field = newValue
        )
    }

    private fun validatePluginSetting(
        setting: PluginSetting,
        newValue: String
    ): ValidatedStringField {
        when (setting.settingType) {
            PluginSettingType.Number -> {
                newValue.toIntOrNull() ?: return ValidatedField.Invalid(
                    field = newValue,
                    error = SettingValidationError.ShouldContainOnlyNumbers,
                )
            }

            PluginSettingType.Boolean -> {
                if (newValue != BooleanSettingTrue && newValue != BooleanSettingFalse) {
                    return ValidatedField.Invalid(
                        field = newValue,
                        error = SettingValidationError.Other,
                    )
                }
            }

            PluginSettingType.String -> {
                if (newValue.isBlank()) {
                    return ValidatedField.Invalid(
                        field = newValue,
                        error = SettingValidationError.ShouldNotBeBlank,
                    )
                }
            }
        }

        return ValidatedField.Valid(newValue)
    }

    fun uninstallPlugin(plugin: Plugin) {
        pluginUninstaller.uninstall(plugin)

        loadData()
    }

    fun syncPluginCache() {
        viewModelScope.launch {
            pluginCacheSyncUseCase()
        }
    }
}
