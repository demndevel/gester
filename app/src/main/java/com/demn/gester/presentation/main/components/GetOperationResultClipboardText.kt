package com.demn.gester.presentation.main.components

import io.github.demndevel.gester.core.operationresult.BasicOperationResult
import io.github.demndevel.gester.core.operationresult.CommandOperationResult
import io.github.demndevel.gester.core.operationresult.IconOperationResult
import io.github.demndevel.gester.core.operationresult.OperationResult
import io.github.demndevel.gester.core.operationresult.TransitionOperationResult

fun getOperationResultClipboardText(operationResult: OperationResult): String {
    return when (operationResult) {
        is BasicOperationResult -> {
            operationResult.text
        }

        is IconOperationResult -> {
            operationResult.text
        }

        is CommandOperationResult -> {
            operationResult.name
        }

        is TransitionOperationResult -> {
            operationResult.finalText
        }
    }
}
