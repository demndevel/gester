package com.demn.findutil.presentation.settings.ui.plugin_settings

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.demn.findutil.presentation.settings.OnPluginSettingChange
import com.demn.findutil.presentation.settings.PluginSettingsSection
import com.demn.findutil.presentation.settings.ValidatedField
import com.demn.findutil.presentation.settings.ui.SettingsSection
import com.demn.findutil.presentation.settings.ui.primitive_setting_fields.BooleanSetting
import com.demn.findutil.presentation.settings.ui.primitive_setting_fields.IntSetting
import com.demn.findutil.presentation.settings.ui.primitive_setting_fields.StringSetting
import com.demn.findutil.presentation.settings.ui.settingErrorMessage
import com.demn.plugincore.BooleanSettingFalse
import com.demn.plugincore.BooleanSettingTrue
import com.demn.plugincore.PluginSettingType

@Composable
fun PluginSettingsSection(
    pluginSettingsSection: PluginSettingsSection,
    onSettingChange: OnPluginSettingChange
) {
    SettingsSection(
        sectionName = "${pluginSettingsSection.plugin.metadata.pluginName} (${pluginSettingsSection.plugin.metadata.version})",
        settings = {
            if (pluginSettingsSection.settings.isEmpty()) {
                Text("No settings")
            }

            pluginSettingsSection.settings.forEach { pluginSetting ->
                val validatedPluginSetting = pluginSetting.validatedField

                val settingErrorMessage =
                    if (validatedPluginSetting is ValidatedField.Invalid) settingErrorMessage(validatedPluginSetting.error)
                    else ""

                when (validatedPluginSetting.field.settingType) {
                    PluginSettingType.String -> {
                        StringSetting(
                            text = validatedPluginSetting.field.settingName,
                            description = validatedPluginSetting.field.settingDescription,
                            isError = validatedPluginSetting is ValidatedField.Invalid,
                            errorMessage = settingErrorMessage,
                            value = validatedPluginSetting.field.settingValue,
                            onValueChange = {
                                onSettingChange(
                                    pluginSettingsSection.plugin,
                                    validatedPluginSetting.field,
                                    it
                                )
                            }
                        )
                    }

                    PluginSettingType.Number -> {
                        IntSetting(
                            text = validatedPluginSetting.field.settingName,
                            description = validatedPluginSetting.field.settingDescription,
                            value = validatedPluginSetting.field.settingValue,
                            isError = validatedPluginSetting is ValidatedField.Invalid,
                            errorMessage = settingErrorMessage,
                            onValueChange = {
                                onSettingChange(
                                    pluginSettingsSection.plugin,
                                    validatedPluginSetting.field,
                                    it
                                )
                            }
                        )
                    }

                    PluginSettingType.Boolean -> {
                        BooleanSetting(
                            text = validatedPluginSetting.field.settingName,
                            description = validatedPluginSetting.field.settingDescription,
                            checked = when (validatedPluginSetting.field.settingValue) {
                                BooleanSettingTrue -> true
                                else -> false
                            },
                            onCheckedChange = {
                                onSettingChange(
                                    pluginSettingsSection.plugin,
                                    validatedPluginSetting.field,
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