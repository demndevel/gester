package com.demn.findutil.presentation.settings.ui.primitive_setting_fields

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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