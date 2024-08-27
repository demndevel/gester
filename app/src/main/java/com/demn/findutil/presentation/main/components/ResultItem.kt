package com.demn.findutil.presentation.main.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.demn.plugincore.operationresult.*

@Composable
fun ResultItem(
    result: OperationResult,
    index: Int,
    onResultClick: (OperationResult) -> Unit,
    onResultLongClick: (OperationResult) -> Unit,
    modifier: Modifier = Modifier
) {
    if (result is BasicOperationResult) {
        BasicResult(
            text = result.text,
            onResultClick = { onResultClick(result) },
            isFirst = index == 0,
            modifier = modifier.fillMaxWidth(),
            resultType = result.type,
            onResultLongClick = { onResultLongClick(result) }
        )
    }

    if (result is IconOperationResult) {
        BasicResult(
            text = result.text,
            iconUri = result.iconUri,
            isFirst = index == 0,
            onResultClick = { onResultClick(result) },
            modifier = modifier.fillMaxWidth(),
            resultType = result.type,
            onResultLongClick = { onResultLongClick(result) }
        )
    }

    if (result is TransitionOperationResult) {
        ConversionResult(
            leftText = result.initialText,
            leftLabel = result.initialDescription ?: "",
            rightText = result.finalText,
            rightLabel = result.finalDescription ?: "",
            onResultLongClick = { onResultLongClick(result) },
            modifier = modifier.fillMaxWidth()
        )
    }

    if (result is CommandOperationResult) {
        BasicResult(
            text = result.name,
            iconUri = result.iconUri,
            onResultClick = { onResultClick(result) },
            isFirst = index == 0,
            modifier = modifier.fillMaxWidth(),
            resultType = result.type,
            onResultLongClick = { onResultLongClick(result) }
        )
    }
}