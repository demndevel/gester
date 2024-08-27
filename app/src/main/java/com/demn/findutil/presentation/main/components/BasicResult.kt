package com.demn.findutil.presentation.main.components

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.demn.findutil.R
import com.demn.plugincore.operationresult.ResultType

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BasicResult(
    text: String,
    onResultClick: () -> Unit,
    isFirst: Boolean,
    modifier: Modifier = Modifier,
    iconUri: Uri? = null,
    resultType: ResultType = ResultType.Other,
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
                text = getResultTypeText(resultType),
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

@Composable
private fun getResultTypeText(type: ResultType): String {
    return when (type) {
        ResultType.Command -> stringResource(R.string.command_result_type)

        ResultType.Alias -> stringResource(R.string.alias_result_type)

        ResultType.WebLink -> stringResource(R.string.weblink_result_type)

        ResultType.Information -> stringResource(R.string.information_result_type)

        ResultType.Application -> stringResource(R.string.application_result_type)

        ResultType.Other -> stringResource(R.string.other_result_type)

        else -> type.name
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
    )
}