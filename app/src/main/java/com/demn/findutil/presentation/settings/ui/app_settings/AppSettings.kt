package com.demn.findutil.presentation.settings.ui.app_settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.demn.findutil.app_settings.AppBooleanSetting
import com.demn.findutil.app_settings.AppNumerousSetting
import com.demn.findutil.app_settings.AppSetting
import com.demn.findutil.app_settings.AppStringSetting
import com.demn.findutil.presentation.settings.*
import com.demn.findutil.presentation.settings.ui.SettingsSection
import com.demn.findutil.presentation.settings.ui.primitive_setting_fields.BooleanSetting
import com.demn.findutil.presentation.settings.ui.primitive_setting_fields.StringSetting
import com.demn.findutil.presentation.settings.ui.primitive_setting_fields.ValidatingIntSetting
import com.demn.findutil.presentation.settings.ui.settingErrorMessage
import com.demn.plugincore.PluginMetadata
import java.util.*

@Composable
fun AppSettings(
    pluginAvailabilities: List<PluginAvailability>,
    settingSections: List<AppSettingsSection>,
    onAppSettingChange: OnAppSettingChange,
    onAvailabilityChange: (metadata: PluginMetadata, available: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            settingSections.forEach { section ->
                AppSettingsSection(
                    section = section,
                    onFieldUpdate = onAppSettingChange,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            PluginAvailabilitySection(
                pluginAvailabilities = pluginAvailabilities,
                onAvailabilityChange = onAvailabilityChange,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
private fun PluginAvailabilitySection(
    pluginAvailabilities: List<PluginAvailability>,
    onAvailabilityChange: (metadata: PluginMetadata, available: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsSection(
        sectionName = "Plugins",
        settings = {
            pluginAvailabilities.forEach { pluginAvailability ->
                PluginAvailability(
                    pluginMetadata = pluginAvailability.pluginMetadata,
                    available = pluginAvailability.available,
                    onAvailabilityChange = onAvailabilityChange,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        },
        modifier = modifier
    )
}

@Composable
private fun AppSettingsSection(
    section: AppSettingsSection,
    onFieldUpdate: OnAppSettingChange,
    modifier: Modifier = Modifier,
) {
    val settings = section.settings

    SettingsSection(
        sectionName = section.title,
        settings = {
            settings.forEach { settingField ->
                AppSetting(
                    settingField = settingField,
                    onFieldUpdate = onFieldUpdate,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        },
        modifier = modifier
    )
}

@Composable
private fun AppSetting(
    settingField: SettingField<AppSetting>,
    onFieldUpdate: OnAppSettingChange,
    modifier: Modifier = Modifier
) {
    val setting = settingField.validatedField.field
    val errorMessage =
        if (settingField.validatedField is ValidatedField.Invalid)
            settingErrorMessage(settingField.validatedField.error)
        else ""

    when (settingField.validatedField.field) {
        is AppStringSetting -> {
            val appStringSetting = settingField.validatedField.field as AppStringSetting
            val stringSettingValue = appStringSetting.value

            StringSetting(
                text = settingField.validatedField.field.title,
                description = setting.description,
                value = stringSettingValue,
                isError = settingField.validatedField is ValidatedField.Invalid,
                errorMessage = errorMessage,
                onValueChange = {
                    onFieldUpdate(appStringSetting.copy(value = it))
                },
                modifier = modifier
            )
        }

        is AppNumerousSetting -> {
            val appNumerousSetting = settingField.validatedField.field as AppNumerousSetting
            val intSettingValue = appNumerousSetting.value

            ValidatingIntSetting( // TODO: this is a temp solution
                text = setting.title,
                description = setting.description,
                initialValue = intSettingValue.toString(),
                onValueChange = {
                    onFieldUpdate(appNumerousSetting.copy(value = it))
                }
            )

//            IntSetting(
//                text = setting.title,
//                description = setting.description,
//                value = intSettingValue.toString(),
////                value = if (intSettingValue == 0) "" else intSettingValue.toString(),
//                isError = settingField.validatedField is ValidatedField.Invalid,
//                errorMessage = errorMessage,
//                onValueChange = { newValue ->
//                    val modifiedValue = try {
//                        newValue.toInt().toString()
//                    } catch (e: NumberFormatException) {
//                        "0$newValue"
//                    }
//
//                    onFieldUpdate(appNumerousSetting.copy(value = modifiedValue.toIntOrNull() ?: 0))
//                },
//                modifier = modifier
//            )
        }

        is AppBooleanSetting -> {
            val appBooleanSetting = settingField.validatedField.field as AppBooleanSetting
            val boolSettingValue = appBooleanSetting.value

            BooleanSetting(
                text = setting.title,
                description = setting.description,
                checked = boolSettingValue,
                onCheckedChange = { onFieldUpdate(appBooleanSetting.copy(value = it)) },
                modifier = modifier
            )
        }
    }
}

val mockSettingSections = listOf(
    AppSettingsSection(
        title = "UI",
        settings = listOf(
            SettingField(
                isEdited = false,
                ValidatedField.Valid(
                    field = AppBooleanSetting(
                        key = UUID.fromString("c34caec8-3f4b-4f67-b1af-defb1d57b8d0"),
                        title = "Transparent UI",
                        description = "Makes some UI elements more transparent",
                        value = true
                    ),
                )
            ),
            SettingField(
                isEdited = false,
                ValidatedField.Invalid(
                    field = AppStringSetting(
                        key = UUID.fromString("7ad1b0c2-b98b-439f-9dd3-ddfdbf8f32e2"),
                        title = "Random setting",
                        description = "Random description",
                        value = ""
                    ),
                    error = SettingValidationError.ShouldNotBeBlank
                )
            )
        )
    ),
    AppSettingsSection(
        title = "Numbers",
        settings = listOf(
            SettingField(
                isEdited = false,
                ValidatedField.Valid(
                    field = AppNumerousSetting(
                        key = UUID.fromString("544a5d1a-529a-40e7-982c-97bdbabdbb2b"),
                        title = "Fav number",
                        description = "Enter your most favourite number",
                        value = 123
                    )
                )
            )
        )
    )
)

@Preview
@Composable
fun AppSettingsPreview() {
    MaterialTheme {
        Surface(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            AppSettings(
                pluginAvailabilities = emptyList(),
                settingSections = mockSettingSections,
                onAppSettingChange = {},
                onAvailabilityChange = { _, _ -> },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}