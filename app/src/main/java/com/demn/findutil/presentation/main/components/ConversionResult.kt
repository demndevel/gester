package com.demn.findutil.presentation.main.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ConversionResult(
    leftText: String,
    leftLabel: String,
    rightText: String,
    rightLabel: String,
    onResultLongClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .combinedClickable(
                onClick = {},
                onLongClick = onResultLongClick,
            ),
    ) {
        Row(
            Modifier
                .padding(8.dp)
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ConversionBlock(leftText, leftLabel)

            Spacer(modifier = Modifier.width(8.dp))

            VerticalDivider(Modifier)

            Spacer(modifier = Modifier.width(8.dp))

            ConversionBlock(rightText, rightLabel)
        }
    }
}

@Composable
private fun ConversionBlock(text: String, labelText: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineLarge,
            fontSize = calculateTextSize(text.length).sp,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(150.dp)
                .height(50.dp)
        )

        Spacer(Modifier.height(8.dp))

        Label(labelText)
    }
}

@Composable
private fun calculateTextSize(length: Int): Int {
    return when (length) {
        in 1..5 -> 32
        in 5..10 -> 25
        in 10..15 -> 22
        in 15..20 -> 18
        in 20..25 -> 15
        else -> 10
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
            fontSize = calculateLabelSize(text.length).sp,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = modifier
                .padding(4.dp)
                .width(100.dp)
                .height(15.dp),
        )
    }
}

@Composable
private fun calculateLabelSize(length: Int): Int {
    return when (length) {
        else -> 11
    }
}

@Preview
@Composable
private fun ConversionResultPreview_SmallText() {
    Box(Modifier.fillMaxSize()) {
        ConversionResult(
            leftText = "5$",
            leftLabel = "american dollar",
            rightText = "500₽",
            rightLabel = "russian rouble",
        )
    }
}

@Preview
@Composable
private fun ConversionResultPreview_MediumText() {
    Box(Modifier.fillMaxSize()) {
        ConversionResult(
            leftText = "(5872 + 57) + 89 * 1337 - 889",
            leftLabel = "medium text medium text medium text medium text medium text ",
            rightText = "(5872 + 57) + 89 * 1337 - 889",
            rightLabel = "medium text medium text medium text",
        )
    }
}

@Preview
@Composable
private fun ConversionResultPreview_LargeText() {
    Box(Modifier.fillMaxSize()) {
        ConversionResult(
            leftText = "(5872 kilometers + 57 meters) + 89 cm * 1337 mm - 889 km + (5872 kilometers + 57 meters) + 89 cm * 1337 mm - 889 km",
            leftLabel = "some large large large large large large large large large text",
            rightText = "(5872 kilometers + 57 meters) + 89 cm * 1337 mm - 889 km + (5872 kilometers + 57 meters) + 89 cm * 1337 mm - 889 km + (5872 kilometers + 57 meters) + 89 cm * 1337 mm - 889 km",
            rightLabel = "again very very very large text very large text very large text – right label",
        )
    }
}
