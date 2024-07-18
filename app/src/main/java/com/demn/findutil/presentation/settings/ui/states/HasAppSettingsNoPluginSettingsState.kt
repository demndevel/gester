package com.demn.findutil.presentation.settings.ui.states

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.demn.findutil.presentation.settings.OnAppSettingChange
import com.demn.findutil.presentation.settings.SettingsScreenUiState
import com.demn.findutil.presentation.settings.ui.InvalidSettingsError
import com.demn.findutil.presentation.settings.ui.app_settings.AppSettings

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
        if (state.hasInvalidSettings) {
            InvalidSettingsError(Modifier.fillMaxWidth())

            HorizontalDivider()
        }

        HorizontalDivider()

        AppSettings(
            settingSections = state.appSettingsSections,
            onAppSettingChange = onAppSettingChange
        )
    }
}