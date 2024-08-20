package com.demn.findutil.presentation.main.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.demn.domain.models.PluginFallbackCommand
import java.util.*

@Composable
fun FallbackCommandsResult(
    fallbackCommand: PluginFallbackCommand,
    onFallbackCommandClick: (id: UUID) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onFallbackCommandClick(fallbackCommand.uuid) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            UriIcon(
                fallbackCommand.iconUri,
                Modifier
                    .size(32.dp)
                    .clip(CircleShape)
            )

            Text(
                text = fallbackCommand.name,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}