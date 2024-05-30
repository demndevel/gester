package com.demn.plugincore

import android.content.Intent
import android.os.Parcelable
import com.demn.plugincore.operation_result.BasicOperationResult
import com.demn.plugincore.operation_result.OperationResult
import com.demn.plugincore.operation_result.TransitionOperationResult
import kotlinx.parcelize.Parcelize

/**
 * There should be set either text or initial&final texts.
 *
 * This is a parcelable implementation of the [OperationResult]. Parcelable used because inheritance is too hard to implement in Parcelable API
 */
@Parcelize
class ParcelableOperationResult private constructor(
    val text: String? = null,
    val description: String? = null,
    val initialText: String? = null,
    val initialDescription: String? = null,
    val finalText: String? = null,
    val finalDescription: String? = null,
    val intent: Intent? = null
) : Parcelable {
    companion object {
        /**
         * Makes parcelable operation result from operation result interface
         */
        fun buildParcelableOperationResult(operationResult: OperationResult): ParcelableOperationResult {
            return when (operationResult) {
                is BasicOperationResult -> ParcelableOperationResult(
                    text = operationResult.text,
                    description = operationResult.description,
                    intent = operationResult.intent
                )

                is TransitionOperationResult -> ParcelableOperationResult(
                    initialText = operationResult.initialText,
                    initialDescription = operationResult.initialDescription,
                    finalText = operationResult.finalText,
                    finalDescription = operationResult.finalDescription
                )
            }
        }
    }
}

/**
 * Convert parcelable operation result to the normal [OperationResult]
 *
 * There should be set either text or initial&final texts.
 *
 * @throws[IllegalArgumentException] when the incorrect parameters entered
 */
fun ParcelableOperationResult.toOperationResult(): OperationResult {
    if (text != null) {
        return BasicOperationResult(
            text,
            description,
            intent
        )
    }

    if (initialText != null && finalText != null) {
        return TransitionOperationResult(
            initialText,
            initialDescription,
            finalText,
            finalDescription
        )
    }

    throw IllegalArgumentException("Incorrect ParcelableOperationResult properties")
}