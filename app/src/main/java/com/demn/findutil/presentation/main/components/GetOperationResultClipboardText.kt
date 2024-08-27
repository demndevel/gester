package com.demn.findutil.presentation.main.components

import com.demn.plugincore.operationresult.*

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