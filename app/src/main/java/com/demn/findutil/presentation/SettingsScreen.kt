package com.demn.findutil.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PlatformImeOptions
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        UnfilledSettingsError(
            modifier = Modifier.fillMaxWidth()
        )

        HorizontalDivider()

        SettingsSection(
            sectionName = "Plugins",
            settings = {
                BooleanSetting(
                    text = "Use external plugins",
                    description = "You can use external plugins that you can download from the Store",
                    checked = true,
                    onCheckedChange = { TODO() }
                )

                BooleanSetting(
                    text = "Foo Plugin",
                    description = "Foo Plugin helps you with solving a big cluster of problems",
                    checked = false,
                    onCheckedChange = { TODO() }
                )

                BooleanSetting(
                    text = "Bar Plugin",
                    description = "Bar Plugin helps you with solving a really small (tiny) cluster of problems",
                    checked = true,
                    onCheckedChange = { TODO() }
                )
            }
        )

        SettingsSection(
            sectionName = "Foo Plugin",
            settings =
            {
                BooleanSetting(
                    text = "Add \"cat\" before each search result?",
                    description = "This setting will add to every search prefix \"cat\"",
                    checked = false,
                    onCheckedChange = { TODO() }
                )

                var yourName by remember { mutableStateOf("") }

                StringSetting(
                    text = "Your name",
                    description = "Your name that will be used in the plugin",
                    value = yourName,
                    onValueChange = { yourName = it }
                )

                var yourAge by remember { mutableStateOf("") }

                IntSetting(
                    text = "Your age (0-100)",
                    description = "Your age that will be used in the plugin",
                    value = yourAge,
                    onValueChange = { yourAge = it }
                )
            }
        )
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
            text = "You have unmeowmeowmeowed some settings",
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
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            label = { Text(text) },
            supportingText = { Text(description) },
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
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            label = { Text(text) },
            supportingText = { Text(description) },
            value = value,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            onValueChange = { newValue ->
                onValueChange(newValue.trimStart { it == '0' })
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
        SettingsScreen(Modifier.fillMaxSize())
    }
}