package com.demn.findutil.presentation.settings.ui.states

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.demn.findutil.presentation.settings.OnAppSettingChange
import com.demn.findutil.presentation.settings.OnPluginSettingChange
import com.demn.findutil.presentation.settings.SettingsScreenUiState
import com.demn.findutil.presentation.settings.ui.UnfilledSettingsError
import com.demn.findutil.presentation.settings.ui.app_settings.AppSettings
import com.demn.findutil.presentation.settings.ui.plugin_settings.PluginSettingsSection

@Composable
fun HasAppSettingsHasPluginSettingsState(
    state: SettingsScreenUiState.HasAppSettingsHasPluginSettings,
    onPluginSettingChange: OnPluginSettingChange,
    onAppSettingChange: OnAppSettingChange,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        UnfilledSettingsError(Modifier.fillMaxWidth()) // todo

        HorizontalDivider()

        AppSettings(
            settingSections = state.appSettingsSections,
            onAppSettingChange = onAppSettingChange,
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            state.pluginSettingsSections.forEach { settingsSection ->
                PluginSettingsSection(settingsSection, onPluginSettingChange)
            }
        }
    }
}