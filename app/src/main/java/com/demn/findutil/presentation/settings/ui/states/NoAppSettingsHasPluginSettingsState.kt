package com.demn.findutil.presentation.settings.ui.states

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.demn.findutil.presentation.settings.OnPluginSettingChange
import com.demn.findutil.presentation.settings.SettingsScreenUiState
import com.demn.findutil.presentation.settings.ui.plugin_settings.PluginSettingsSection
import com.demn.findutil.presentation.settings.ui.InvalidSettingsError

@Composable
fun NoAppSettingsHasPluginSettingsState(
    onPluginSettingChange: OnPluginSettingChange,
    modifier: Modifier,
    state: SettingsScreenUiState.NoAppSettingsHasPluginSettings,
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

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.pluginSettingsSections) { settingsSection ->
                PluginSettingsSection(settingsSection, onPluginSettingChange)
            }
        }
    }
}