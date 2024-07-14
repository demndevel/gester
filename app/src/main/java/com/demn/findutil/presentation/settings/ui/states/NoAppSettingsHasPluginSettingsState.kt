package com.demn.findutil.presentation.settings.ui.states

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.demn.findutil.presentation.settings.OnSettingChange
import com.demn.findutil.presentation.settings.SettingsScreenUiState
import com.demn.findutil.presentation.settings.ui.PluginSettingsSection
import com.demn.findutil.presentation.settings.ui.UnfilledSettingsError

@Composable
fun NoAppSettingsHasPluginSettingsState(
    onSettingChange: OnSettingChange,
    modifier: Modifier,
    state: SettingsScreenUiState.NoAppSettingsHasPluginSettings,
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        UnfilledSettingsError(Modifier.fillMaxWidth())

        HorizontalDivider()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.pluginSettingsSections) { settingsSection ->
                PluginSettingsSection(settingsSection, onSettingChange)
            }
        }
    }
}