package com.demn.findutil.presentation.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ConversionResult(
    leftText: String,
    leftLabel: String,
    rightText: String,
    rightLabel: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(IntrinsicSize.Min),
    ) {
        Row(
            Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
            ) {
                Text(
                    text = leftText,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                )

                Spacer(Modifier.height(8.dp))

                Label(leftLabel)
            }

            Spacer(modifier = Modifier.width(8.dp))

            VerticalDivider(Modifier) // here

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
            ) {
                Text(
                    text = rightText,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                )

                Spacer(Modifier.height(8.dp))

                Label(rightLabel)
            }
        }
    }
}

@Composable
private fun Label(
    text: String,
    modifier: Modifier = Modifier
) {
    Column(
        Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.labelSmall,
            modifier = modifier
                .padding(4.dp),
        )
    }
}

@Preview
@Composable
private fun ConversionResultPreview() {
    Box(Modifier.fillMaxSize()) {
        ConversionResult(
            leftText = "5$",
            leftLabel = "american dollar",
            rightText = "500₽",
            rightLabel = "russian rouble",
        )
    }
}
