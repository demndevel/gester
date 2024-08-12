package com.demn.findutil.presentation.settings.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.demn.findutil.R
import com.demn.findutil.app_settings.MockAppSettingsRepositoryImpl
import com.demn.findutil.presentation.settings.*
import com.demn.findutil.presentation.settings.ui.states.HasDataState
import com.demn.findutil.presentation.settings.ui.states.LoadingState
import com.demn.findutil.presentation.settings.ui.states.NoDataState
import com.demn.plugincore.parcelables.PluginMetadata
import com.demn.domain.models.ExternalPlugin
import com.demn.domain.usecase.MockPluginCacheSyncUseCase
import com.demn.findutil.app_settings.MockPluginAvailabilityRepository
import com.demn.pluginloading.MockPluginRepository
import com.demn.pluginloading.MockPluginSettingsRepository
import com.demn.pluginloading.MockPluginUninstaller
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    vm: SettingsScreenViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadData()
    }

    Scaffold(
        modifier = modifier
            .imePadding(),
        floatingActionButton = {
            SaveButton(
                visible = state.saveButtonVisible,
                onClick = vm::save
            )
        },
        containerColor = Color.Transparent
    ) { contentPadding ->
        Card(
            modifier = Modifier
                .padding(contentPadding)
                .padding(8.dp)
                .fillMaxSize(),
            elevation = CardDefaults
                .elevatedCardElevation(defaultElevation = 6.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            SettingsScreenState(
                state = state,
                onAppSettingChange = vm::setAppSetting,
                onBooleanAppSettingUpdate = vm::setBooleanAppSetting,
                onPluginSettingChange = vm::setPluginSetting,
                onAvailabilityChange = vm::setPluginAvailability,
                onPluginUninstall = vm::uninstallPlugin,
                onPluginCacheSync = vm::syncPluginCache,
                modifier = Modifier
                    .fillMaxSize(),
            )
        }
    }
}

@Composable
private fun SettingsScreenState(
    state: SettingsScreenUiState,
    onAppSettingChange: OnAppSettingChange,
    onBooleanAppSettingUpdate: OnAppBooleanSettingChange,
    onPluginSettingChange: OnPluginSettingChange,
    onAvailabilityChange: (metadata: PluginMetadata, available: Boolean) -> Unit,
    onPluginUninstall: (ExternalPlugin) -> Unit,
    onPluginCacheSync: () -> Unit,
    modifier: Modifier,
) {
    when (state) {
        is SettingsScreenUiState.Loading -> {
            LoadingState(
                modifier
            )
        }

        is SettingsScreenUiState.NoData -> {
            NoDataState(
                modifier
            )
        }


        is SettingsScreenUiState.HasDataState -> {
            HasDataState(
                state = state,
                onPluginSettingChange = onPluginSettingChange,
                onAppSettingChange = onAppSettingChange,
                onBooleanFieldUpdate = onBooleanAppSettingUpdate,
                onAvailabilityChange = onAvailabilityChange,
                onPluginUninstall = onPluginUninstall,
                onPluginCacheSync = onPluginCacheSync,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun SaveButton(
    visible: Boolean,
    onClick: () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(),
        exit = scaleOut(),
    ) {
        FloatingActionButton(onClick = onClick) {
            Icon(
                painterResource(R.drawable.save_icon),
                contentDescription = null
            )
        }
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    Box(Modifier.fillMaxSize()) {
        SettingsScreen(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            vm = SettingsScreenViewModel(
                MockPluginSettingsRepository(),
                MockAppSettingsRepositoryImpl(),
                MockPluginRepository(),
                MockPluginAvailabilityRepository(),
                MockPluginUninstaller(),
                MockPluginCacheSyncUseCase()
            )
        )
    }
}