package com.demn.findutil.presentation.settings.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.demn.findutil.R
import com.demn.findutil.app_settings.MockAppSettingsRepositoryImpl
import com.demn.findutil.presentation.settings.OnAppSettingChange
import com.demn.findutil.presentation.settings.OnPluginSettingChange
import com.demn.findutil.presentation.settings.SettingsScreenUiState
import com.demn.findutil.presentation.settings.SettingsScreenViewModel
import com.demn.findutil.presentation.settings.ui.states.*
import com.demn.pluginloading.MockPluginSettingsRepository
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
        modifier = modifier,
        floatingActionButton = {
            SaveButton(
                visible = state.saveButtonVisible,
                onClick = vm::save
            )
        }
    ) { contentPadding ->
        SettingsScreenState(
            state = state,
            contentPadding = contentPadding,
            onPluginSettingChange = vm::setPluginSetting,
            onAppSettingChange = vm::setAppSetting,
        )
    }
}

@Composable
private fun SettingsScreenState(
    state: SettingsScreenUiState,
    contentPadding: PaddingValues,
    onAppSettingChange: OnAppSettingChange,
    onPluginSettingChange: OnPluginSettingChange,
) {
    when (state) {
        is SettingsScreenUiState.Loading -> {
            LoadingState(
                Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            )
        }

        is SettingsScreenUiState.NoData -> {
            NoDataState(
                Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            )
        }

        is SettingsScreenUiState.NoAppSettingsHasPluginSettings -> {
            NoAppSettingsHasPluginSettingsState(
                onPluginSettingChange = onPluginSettingChange,
                state = state,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            )
        }

        is SettingsScreenUiState.HasAppSettingsHasPluginSettings -> {
            HasAppSettingsHasPluginSettingsState(
                onAppSettingChange = onAppSettingChange,
                onPluginSettingChange = onPluginSettingChange,
                state = state,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            )
        }

        is SettingsScreenUiState.HasAppSettingsNoPluginSettings -> {
            HasAppSettingsNoPluginSettingsState(
                onAppSettingChange = onAppSettingChange,
                state = state,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
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
        enter = fadeIn(),
        exit = fadeOut()
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
    Surface(Modifier.fillMaxSize()) {
        SettingsScreen(
            modifier = Modifier
                .fillMaxSize(),
            vm = SettingsScreenViewModel(
                MockPluginSettingsRepository(),
                MockAppSettingsRepositoryImpl()
            )
        )
    }
}