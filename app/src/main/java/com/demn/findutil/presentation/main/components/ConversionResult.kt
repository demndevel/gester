package com.demn.findutil.presentation.main.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ConversionResult(
    startText: String,
    startLabel: String,
    finalText: String,
    finalLabel: String,
    modifier: Modifier = Modifier,
    onResultLongClick: () -> Unit = {},
) {
    Card(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .combinedClickable(
                onClick = {},
                onLongClick = onResultLongClick,
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = "$startText =",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = finalText,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = startLabel,
                    style = MaterialTheme.typography.labelSmall
                )

                Icon(
                    Icons.AutoMirrored.Default.KeyboardArrowRight,
                    null,
                    modifier = Modifier.size(16.dp)
                )

                Text(
                    text = finalLabel,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Preview
@Composable
private fun ConversionResultPreview_SmallText() {
    Box(Modifier.fillMaxSize()) {
        ConversionResult(
            startText = "5$",
            startLabel = "american dollar",
            finalText = "500₽",
            finalLabel = "russian rouble",
        )
    }
}

@Preview
@Composable
private fun ConversionResultPreview_MediumText() {
    Box(Modifier.fillMaxSize()) {
        ConversionResult(
            startText = "(5872 + 57) + 89 * 1337 - 889",
            startLabel = "medium text medium text medium text medium text medium text ",
            finalText = "(5872 + 57) + 89 * 1337 - 889",
            finalLabel = "medium text medium text medium text",
        )
    }
}

@Preview
@Composable
private fun ConversionResultPreview_LargeText() {
    Box(Modifier.fillMaxSize()) {
        ConversionResult(
            startText = "(5872 kilometers + 57 meters) + 89 cm * 1337 mm - 889 km + (5872 kilometers + 57 meters) + 89 cm * 1337 mm - 889 km",
            startLabel = "some large large large large large large large large large text",
            finalText = "(5872 kilometers + 57 meters) + 89 cm * 1337 mm - 889 km + (5872 kilometers + 57 meters) + 89 cm * 1337 mm - 889 km + (5872 kilometers + 57 meters) + 89 cm * 1337 mm - 889 km",
            finalLabel = "again very very very large text very large text very large text – right label",
        )
    }
}
