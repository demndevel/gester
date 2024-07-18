package com.demn.findutil.presentation.settings.ui.primitive_setting_fields

import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.demn.findutil.R

@Composable
fun ValidatingIntSetting(
    text: String,
    description: String,
    initialValue: String,
    onValueChange: (Int) -> Unit
) {
    var fieldValue by remember { mutableStateOf(initialValue) }
    val isParsed = fieldValue.toIntOrNull() != null

    IntSetting(
        text = text,
        description = description,
        value = fieldValue,
        isError = !isParsed,
        errorMessage = stringResource(R.string.should_contain_only_numbers_setting_error),
        onValueChange = {
            fieldValue = it
            if (fieldValue.toIntOrNull() != null) onValueChange(fieldValue.toInt())
        }
    )
}