package com.demn.gester.presentation.settings.ui.app_settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.demndevel.gester.core.parcelables.PluginMetadata

@Composable
fun PluginAvailability(
    pluginMetadata: PluginMetadata,
    available: Boolean,
    onAvailabilityChange: (metadata: PluginMetadata, available: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = pluginMetadata.pluginName,
                style = MaterialTheme.typography.bodyLarge,
            )

            pluginMetadata.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Switch(
            checked = available,
            onCheckedChange = {
                onAvailabilityChange(pluginMetadata, it)
            },
            modifier = Modifier
        )
    }
}
