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
import com.demn.findutil.presentation.settings.*
import com.demn.findutil.presentation.settings.ui.InvalidSettingsError
import com.demn.findutil.presentation.settings.ui.app_settings.AppSettings
import com.demn.findutil.presentation.settings.ui.plugin_settings.PluginSettingsSection
import com.demn.plugincore.parcelables.PluginMetadata
import com.demn.domain.models.Plugin

@Composable
fun HasDataState(
    state: SettingsScreenUiState.HasDataState,
    onPluginSettingChange: OnPluginSettingChange,
    onAppSettingChange: OnAppSettingChange,
    onBooleanFieldUpdate: OnAppBooleanSettingChange,
    onAvailabilityChange: (metadata: PluginMetadata, available: Boolean) -> Unit,
    onPluginUninstall: (Plugin) -> Unit,
    onPluginCacheSync: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (state.hasInvalidSettings) {
            InvalidSettingsError(Modifier.fillMaxWidth())

            HorizontalDivider()
        }

        AppSettings(
            appSettings = state.appSettings,
            onAppSettingChange = onAppSettingChange,
            onBooleanFieldUpdate = onBooleanFieldUpdate,
            onAvailabilityChange = onAvailabilityChange,
            pluginAvailabilities = state.pluginAvailabilities,
            onPluginCacheSync = onPluginCacheSync,
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            state.pluginSettingsSections.forEach { settingsSection ->
                PluginSettingsSection(settingsSection, onPluginSettingChange, onPluginUninstall)
            }
        }
    }
}