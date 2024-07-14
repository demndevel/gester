package com.demn.findutil.presentation.settings.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
            text = "You have unfilled some settings", // todo use string resources
            style = MaterialTheme.typography.bodyLarge
        )
    }
}