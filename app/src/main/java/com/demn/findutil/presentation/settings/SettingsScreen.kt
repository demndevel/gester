package com.demn.findutil.presentation.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.demn.findutil.R
import com.demn.findutil.plugins.MockPluginSettingsRepository
import com.demn.plugincore.BooleanSettingFalse
import com.demn.plugincore.BooleanSettingTrue
import com.demn.plugincore.PluginSettingType
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
            SaveButton(state.saveButtonVisible, onClick = vm::save)
        }
    ) { contentPadding ->
        SettingsScreenState(state, onSettingChange = vm::setPluginSetting, contentPadding)
    }
}

@Composable
private fun SettingsScreenState(
    state: SettingsScreenUiState,
    onSettingChange: OnSettingChange,
    contentPadding: PaddingValues
) {
    when (state) {
        is SettingsScreenUiState.Loading -> {
            LoadingState(Modifier.fillMaxSize())
        }

        is SettingsScreenUiState.NoData -> {
            Text("no data")
        }

        is SettingsScreenUiState.NoAppSettingsHasPluginSettings -> {
            NoAppSettingsHasPluginSettings(
                onSettingChange = onSettingChange,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                state = state
            )
        }

        is SettingsScreenUiState.HasAppSettingsHasPluginSettings -> {
            Text("HasAppSettingsHasPluginSettings")
        }

        is SettingsScreenUiState.HasAppSettingsNoPluginSettings -> {
            Text("HasAppSettingsNoPluginSettings")
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
            Icon(painterResource(R.drawable.save_icon), contentDescription = null)
        }
    }
}

@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(Modifier.size(64.dp))
    }
}

@Composable
private fun NoAppSettingsHasPluginSettings(
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

@Composable
private fun PluginSettingsSection(
    settingsSection: SettingsSection,
    onSettingChange: OnSettingChange
) {
    SettingsSection(
        sectionName = "${settingsSection.plugin.metadata.pluginName} (${settingsSection.plugin.metadata.version})",
        settings = {
            if (settingsSection.settings.isEmpty()) {
                Text("No settings")
            }

            settingsSection.settings.forEach { pluginSetting ->
                val validatedPluginSetting = pluginSetting.validatedField

                val settingErrorMessage =
                    if (validatedPluginSetting is ValidatedField.InValid) settingErrorMessage(validatedPluginSetting.error)
                    else ""

                when (validatedPluginSetting.value.settingType) {
                    PluginSettingType.String -> {
                        StringSetting(
                            text = validatedPluginSetting.value.settingName,
                            description = validatedPluginSetting.value.settingDescription,
                            isError = validatedPluginSetting is ValidatedField.InValid,
                            errorMessage = settingErrorMessage,
                            value = validatedPluginSetting.value.settingValue,
                            onValueChange = {
                                onSettingChange(
                                    settingsSection.plugin,
                                    validatedPluginSetting.value,
                                    it
                                )
                            }
                        )
                    }

                    PluginSettingType.Number -> {
                        IntSetting(
                            text = validatedPluginSetting.value.settingName,
                            description = validatedPluginSetting.value.settingDescription,
                            value = validatedPluginSetting.value.settingValue,
                            isError = validatedPluginSetting is ValidatedField.InValid,
                            errorMessage = settingErrorMessage,
                            onValueChange = {
                                onSettingChange(
                                    settingsSection.plugin,
                                    validatedPluginSetting.value,
                                    it
                                )
                            }
                        )
                    }

                    PluginSettingType.Boolean -> {
                        BooleanSetting(
                            text = validatedPluginSetting.value.settingName,
                            description = validatedPluginSetting.value.settingDescription,
                            checked = true,
                            onCheckedChange = {
                                onSettingChange(
                                    settingsSection.plugin,
                                    validatedPluginSetting.value,
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

@Composable
private fun settingErrorMessage(error: SettingValidationError): String {
    return when (error) {
        SettingValidationError.ShouldContainOnlyNumbers -> stringResource(R.string.should_contain_only_numbers_setting_error)
        SettingValidationError.ShouldNotBeBlank -> stringResource(R.string.should_not_be_blank_setting_error)
        SettingValidationError.Other -> stringResource(id = R.string.other_setting_error)
    }
}

@Composable
fun UnfilledSettingsError(modifier: Modifier = Modifier) {
    Row(modifier) {
        Icon(
            imageVector = Icons.Default.Warning,
            tint = MaterialTheme.colorScheme.error,
            contentDescription = null
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = "You have unfilled some settings",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun SettingsSection(
    sectionName: String,
    settings: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(
            text = sectionName,
            style = MaterialTheme.typography.labelMedium
        )

        Spacer(Modifier.height(12.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            settings()
        }

        Spacer(Modifier.height(12.dp))

        HorizontalDivider()
    }
}

@Composable
fun BooleanSetting(
    text: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier) {
        Column(Modifier.weight(1f)) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.width(8.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier
        )
    }
}

@Composable
fun StringSetting(
    text: String,
    description: String,
    value: String,
    isError: Boolean,
    errorMessage: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            label = { Text(text) },
            supportingText = {
                if (isError) Text(errorMessage)
                else Text(description)
            },
            isError = isError,
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
fun IntSetting(
    text: String,
    description: String,
    value: String,
    isError: Boolean,
    errorMessage: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            label = { Text(text) },
            value = value,
            supportingText = {
                if (isError) Text(errorMessage)
                else Text(description)
            },
            isError = isError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            onValueChange = { newValue ->
                if (newValue.toIntOrNull() != null) onValueChange(newValue.trimStart { it == '0' })
            },
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    Surface(Modifier.fillMaxSize()) {
        SettingsScreen(
            modifier = Modifier.fillMaxSize(),
            vm = SettingsScreenViewModel(MockPluginSettingsRepository())
        )
    }
}