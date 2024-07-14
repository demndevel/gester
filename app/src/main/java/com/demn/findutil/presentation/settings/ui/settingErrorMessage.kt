package com.demn.findutil.presentation.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.demn.findutil.R
import com.demn.findutil.presentation.settings.SettingValidationError

@Composable
fun settingErrorMessage(error: SettingValidationError): String {
    return when (error) {
        SettingValidationError.ShouldContainOnlyNumbers -> stringResource(R.string.should_contain_only_numbers_setting_error)
        SettingValidationError.ShouldNotBeBlank -> stringResource(R.string.should_not_be_blank_setting_error)
        SettingValidationError.Other -> stringResource(id = R.string.other_setting_error)
    }
}