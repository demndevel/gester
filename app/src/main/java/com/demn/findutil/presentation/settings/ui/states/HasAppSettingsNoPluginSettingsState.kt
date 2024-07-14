package com.demn.findutil.presentation.settings.ui.states

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.demn.findutil.presentation.settings.OnAppSettingChange
import com.demn.findutil.presentation.settings.SettingsScreenUiState
import com.demn.findutil.presentation.settings.ui.UnfilledSettingsError
import com.demn.findutil.presentation.settings.ui.app_settings.AppSettings
import com.demn.findutil.presentation.settings.ui.plugin_settings.PluginSettingsSection

@Composable
fun HasAppSettingsNoPluginSettingsState(
    onAppSettingChange: OnAppSettingChange,
    state: SettingsScreenUiState.HasAppSettingsNoPluginSettings,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        UnfilledSettingsError(Modifier.fillMaxWidth()) // todo

        HorizontalDivider()

        AppSettings(
            settingSections = state.appSettingsSections,
            onAppSettingChange = onAppSettingChange
        )
    }
}