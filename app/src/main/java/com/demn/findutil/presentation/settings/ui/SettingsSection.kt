package com.demn.findutil.presentation.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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