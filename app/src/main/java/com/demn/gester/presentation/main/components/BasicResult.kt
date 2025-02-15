package com.demn.gester.presentation.main.components

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BasicResult(
    text: String,
    onResultClick: () -> Unit,
    isFirst: Boolean,
    modifier: Modifier = Modifier,
    iconUri: Uri? = null,
    resultType: String,
    onResultLongClick: () -> Unit = {},
) {
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = when (isFirst) {
                true -> 6.dp
                false -> 1.dp
            },
            pressedElevation = 12.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = when (isFirst) {
                true -> MaterialTheme.colorScheme.primaryContainer
                false -> MaterialTheme.colorScheme.surface
            }
        ),
        modifier = modifier
            .combinedClickable(
                onClick = { onResultClick() },
                onLongClick = onResultLongClick),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
        ) {
            iconUri?.let {
                UriIcon(
                    iconUri = it,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                )

                Spacer(Modifier.width(8.dp))
            }

            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .weight(1f)
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = resultType,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .border(
                        1.dp,
                        color = LocalContentColor.current,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(4.dp)
            )
        }
    }
}

@Preview
@Composable
private fun BasicResultNotFirstPreview() {
    BasicResult(
        text = "help",
        onResultClick = {},
        false,
        iconUri = null,
        resultType = "Something"
    )
}

@Preview
@Composable
private fun BasicResultFirstPreview() {
    BasicResult(
        text = "help",
        onResultClick = {},
        true,
        iconUri = null,
        resultType = "Something"
    )
}
