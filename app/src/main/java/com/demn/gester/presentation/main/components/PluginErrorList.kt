package com.demn.gester.presentation.main.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.demn.domain.models.PluginError
import com.demn.domain.models.PluginErrorType
import com.demn.gester.R

@Composable
fun PluginErrorList(
    pluginErrorList: List<PluginError>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        pluginErrorList.forEach { error ->
            PluginError(error, Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun PluginError(
    error: PluginError,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp,
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        modifier = modifier
            .height(IntrinsicSize.Min)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = stringResource(R.string.plugin_loading_error),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .weight(1f)
            )

            Text(
                text = error.message,
                style = MaterialTheme.typography.bodySmall,
            )

            Text(
                text = "${stringResource(R.string.plugin_uuid_error_description)}: ${error.pluginId ?: stringResource(R.string.unavailable)}",
                style = MaterialTheme.typography.labelMedium,
            )

            Text(
                text = "${stringResource(R.string.plugin_name_error_description)}: ${error.pluginName ?: stringResource(R.string.unavailable)}",
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Preview
@Composable
fun PluginErrorListPreview(modifier: Modifier = Modifier) {
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        PluginErrorList(
            pluginErrorList = listOf(
                PluginError(
                    pluginId = "someid",
                    pluginName = "Foo plugin",
                    type = PluginErrorType.Unloaded,
                    message = """
                        exception: some exception message
                        additional info: information lorem ipsum
                    """.trimIndent()
                ),
                PluginError(
                    pluginId = "someid",
                    pluginName = "Bar plugin",
                    type = PluginErrorType.Other,
                    message = """
                        exception: some exception message
                        additional info: information lorem ipsum dolor sit amet dolor lorem ipsum dolor sit amet lorem
                    """.trimIndent()
                ),
            )
        )
    }
}
