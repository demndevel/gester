package com.demn.findutil.presentation.settings.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.demn.findutil.presentation.settings.OnSettingChange
import com.demn.findutil.presentation.settings.SettingsSection
import com.demn.findutil.presentation.settings.ValidatedField
import com.demn.findutil.presentation.settings.ui.primitive_setting_fields.BooleanSetting
import com.demn.findutil.presentation.settings.ui.primitive_setting_fields.IntSetting
import com.demn.findutil.presentation.settings.ui.primitive_setting_fields.StringSetting
import com.demn.plugincore.BooleanSettingFalse
import com.demn.plugincore.BooleanSettingTrue
import com.demn.plugincore.PluginSettingType

@Composable
fun PluginSettingsSection(
    settingsSection: SettingsSection,
    onSettingChange: OnSettingChange
) {
    SettingsSection(
        sectionName = "${settingsSection.plugin.metadata.pluginName} (${settingsSection.plugin.metadata.version})",
        settings = {
            if (settingsSection.settings.isEmpty()) {
                Text("No settings")
            }

            settingsSection.settings.forEach { pluginSetting ->
                val validatedPluginSetting = pluginSetting.validatedField

                val settingErrorMessage =
                    if (validatedPluginSetting is ValidatedField.InValid) settingErrorMessage(validatedPluginSetting.error)
                    else ""

                when (validatedPluginSetting.value.settingType) {
                    PluginSettingType.String -> {
                        StringSetting(
                            text = validatedPluginSetting.value.settingName,
                            description = validatedPluginSetting.value.settingDescription,
                            isError = validatedPluginSetting is ValidatedField.InValid,
                            errorMessage = settingErrorMessage,
                            value = validatedPluginSetting.value.settingValue,
                            onValueChange = {
                                onSettingChange(
                                    settingsSection.plugin,
                                    validatedPluginSetting.value,
                                    it
                                )
                            }
                        )
                    }

                    PluginSettingType.Number -> {
                        IntSetting(
                            text = validatedPluginSetting.value.settingName,
                            description = validatedPluginSetting.value.settingDescription,
                            value = validatedPluginSetting.value.settingValue,
                            isError = validatedPluginSetting is ValidatedField.InValid,
                            errorMessage = settingErrorMessage,
                            onValueChange = {
                                onSettingChange(
                                    settingsSection.plugin,
                                    validatedPluginSetting.value,
                                    it
                                )
                            }
                        )
                    }

                    PluginSettingType.Boolean -> {
                        BooleanSetting(
                            text = validatedPluginSetting.value.settingName,
                            description = validatedPluginSetting.value.settingDescription,
                            checked = true,
                            onCheckedChange = {
                                onSettingChange(
                                    settingsSection.plugin,
                                    validatedPluginSetting.value,
                                    when (it) {
                                        true -> BooleanSettingTrue
                                        false -> BooleanSettingFalse
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    )
}