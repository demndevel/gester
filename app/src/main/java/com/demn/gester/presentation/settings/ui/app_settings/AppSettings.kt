package com.demn.gester.presentation.settings.ui.app_settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.demn.gester.R
import com.demn.gester.presentation.settings.*
import com.demn.gester.presentation.settings.ui.SettingsSection
import com.demn.gester.presentation.settings.ui.primitive_setting_fields.BooleanSetting
import com.demn.gester.presentation.settings.ui.primitive_setting_fields.IntSetting
import com.demn.gester.presentation.settings.ui.primitive_setting_fields.StringSetting
import com.demn.gester.presentation.settings.ui.settingErrorMessage
import io.github.demndevel.gester.core.parcelables.PluginMetadata

@Composable
fun AppSettings(
    pluginAvailabilities: List<PluginAvailability>,
    appSettings: List<SettingField<com.demn.domain.models.AppSettingMetadata>>,
    onAppSettingChange: OnAppSettingChange,
    onBooleanFieldUpdate: OnAppBooleanSettingChange,
    onAvailabilityChange: (metadata: PluginMetadata, available: Boolean) -> Unit,
    onPluginCacheSync: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            AppSettingsSection(
                settings = appSettings,
                onFieldUpdate = onAppSettingChange,
                onBooleanFieldUpdate = onBooleanFieldUpdate,
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

        TextButton(
            onClick = onPluginCacheSync,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.manual_plugin_cache_sync))
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
    settings: List<SettingField<com.demn.domain.models.AppSettingMetadata>>,
    onFieldUpdate: OnAppSettingChange,
    onBooleanFieldUpdate: OnAppBooleanSettingChange,
    modifier: Modifier = Modifier,
) {
    SettingsSection(
        sectionName = stringResource(com.demn.gester.R.string.app_settings_section_title),
        settings = {
            settings.forEach { settingField ->
                AppSetting(
                    settingField = settingField,
                    onFieldUpdate = onFieldUpdate,
                    onBooleanFieldUpdate = onBooleanFieldUpdate,
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
    settingField: SettingField<com.demn.domain.models.AppSettingMetadata>,
    onFieldUpdate: OnAppSettingChange,
    onBooleanFieldUpdate: OnAppBooleanSettingChange,
    modifier: Modifier = Modifier
) {
    val settingValue = settingField.validatedField.field
    val errorMessage =
        if (settingField.validatedField is ValidatedField.Invalid)
            settingErrorMessage(settingField.validatedField.error)
        else ""

    when (settingField.settingMetadata.settingType) {
        com.demn.domain.models.AppSettingType.String -> {
            StringSetting(
                text = settingField.settingMetadata.title,
                description = settingField.settingMetadata.description,
                value = settingValue,
                isError = settingField.validatedField is ValidatedField.Invalid,
                errorMessage = errorMessage,
                onValueChange = {
                    onFieldUpdate(settingField.settingMetadata, it)
                },
                modifier = modifier
            )
        }

        com.demn.domain.models.AppSettingType.Numerous -> {
            IntSetting(
                text = settingField.settingMetadata.title,
                description = settingField.settingMetadata.description,
                value = settingValue,
                isError = settingField.validatedField is ValidatedField.Invalid,
                errorMessage = errorMessage,
                onValueChange = { newValue ->
                    onFieldUpdate(settingField.settingMetadata, newValue)
                },
                modifier = modifier
            )
        }

        com.demn.domain.models.AppSettingType.Boolean -> {
            BooleanSetting(
                text = settingField.settingMetadata.title,
                description = settingField.settingMetadata.description,
                checked = when (settingValue) { // TODO: to review this
                    true.toString() -> true
                    else -> false
                },
                onCheckedChange = { onBooleanFieldUpdate(settingField.settingMetadata, it) },
                modifier = modifier
            )
        }
    }
}

//val mockSettingSections = listOf(
//    AppSettingsSection(
//        title = "UI",
//        settings = listOf(
//            SettingField(
//                isEdited = false,
//                ValidatedField.Valid(
//                    field = AppBooleanSetting(
//                        key = UUID.fromString("c34caec8-3f4b-4f67-b1af-defb1d57b8d0"),
//                        title = "Transparent UI",
//                        description = "Makes some UI elements more transparent",
//                        value = true
//                    ),
//                )
//            ),
//            SettingField(
//                isEdited = false,
//                ValidatedField.Invalid(
//                    field = AppStringSetting(
//                        key = UUID.fromString("7ad1b0c2-b98b-439f-9dd3-ddfdbf8f32e2"),
//                        title = "Random setting",
//                        description = "Random description",
//                        value = ""
//                    ),
//                    error = SettingValidationError.ShouldNotBeBlank
//                )
//            )
//        )
//    ),
//    AppSettingsSection(
//        title = "Numbers",
//        settings = listOf(
//            SettingField(
//                isEdited = false,
//                ValidatedField.Valid(
//                    field = AppNumerousSetting(
//                        key = UUID.fromString("544a5d1a-529a-40e7-982c-97bdbabdbb2b"),
//                        title = "Fav number",
//                        description = "Enter your most favourite number",
//                        value = 123
//                    )
//                )
//            )
//        )
//    )
//)

//@Preview
//@Composable
//fun AppSettingsPreview() {
//    MaterialTheme {
//        Surface(
//            Modifier
//                .fillMaxSize()
//                .padding(16.dp)
//        ) {
//            AppSettings(
//                pluginAvailabilities = emptyList(),
//                settingSections = mockSettingSections,
//                onAppSettingChange = {},
//                onAvailabilityChange = { _, _ -> },
//                modifier = Modifier.fillMaxSize(),
//            )
//        }
//    }
//}
