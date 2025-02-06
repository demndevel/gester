package com.demn.gester.presentation.settings.ui.plugin_settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.demn.gester.R
import com.demn.gester.presentation.settings.OnPluginSettingChange
import com.demn.gester.presentation.settings.PluginSettingsSection
import com.demn.gester.presentation.settings.ValidatedField
import com.demn.gester.presentation.settings.ui.SettingsSection
import com.demn.gester.presentation.settings.ui.primitive_setting_fields.BooleanSetting
import com.demn.gester.presentation.settings.ui.primitive_setting_fields.IntSetting
import com.demn.gester.presentation.settings.ui.primitive_setting_fields.StringSetting
import com.demn.gester.presentation.settings.ui.settingErrorMessage
import com.demn.domain.models.Plugin
import io.github.demndevel.gester.core.BooleanSettingFalse
import io.github.demndevel.gester.core.BooleanSettingTrue
import io.github.demndevel.gester.core.parcelables.PluginSettingType

@Composable
fun PluginSettingsSection(
    pluginSettingsSection: PluginSettingsSection,
    onSettingChange: OnPluginSettingChange,
    onUninstall: (Plugin) -> Unit,
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

                when (pluginSetting.settingMetadata.settingType) {
                    PluginSettingType.String -> {
                        StringSetting(
                            text = pluginSetting.settingMetadata.settingName,
                            description = pluginSetting.settingMetadata.settingDescription,
                            isError = validatedPluginSetting is ValidatedField.Invalid,
                            errorMessage = settingErrorMessage,
                            value = validatedPluginSetting.field,
                            onValueChange = {
                                onSettingChange(
                                    pluginSettingsSection.plugin,
                                    pluginSetting.settingMetadata,
                                    it
                                )
                            }
                        )
                    }

                    PluginSettingType.Number -> {
                        IntSetting(
                            text = pluginSetting.settingMetadata.settingName,
                            description = pluginSetting.settingMetadata.settingDescription,
                            value = validatedPluginSetting.field,
                            isError = validatedPluginSetting is ValidatedField.Invalid,
                            errorMessage = settingErrorMessage,
                            onValueChange = {
                                onSettingChange(
                                    pluginSettingsSection.plugin,
                                    pluginSetting.settingMetadata,
                                    it
                                )
                            }
                        )
                    }

                    PluginSettingType.Boolean -> {
                        BooleanSetting(
                            text = pluginSetting.settingMetadata.settingName,
                            description = pluginSetting.settingMetadata.settingDescription,
                            checked = when (validatedPluginSetting.field) {
                                BooleanSettingTrue -> true
                                else -> false
                            },
                            onCheckedChange = {
                                onSettingChange(
                                    pluginSettingsSection.plugin,
                                    pluginSetting.settingMetadata,
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

            if (pluginSettingsSection.plugin is Plugin) {
                TextButton(
                    onClick = { onUninstall(pluginSettingsSection.plugin) },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    val uninstallText =
                        "${stringResource(id = R.string.uninstall_pluginname)} ${pluginSettingsSection.plugin.metadata.pluginName}"

                    Text(text = uninstallText)
                }
            }
        }
    )
}
