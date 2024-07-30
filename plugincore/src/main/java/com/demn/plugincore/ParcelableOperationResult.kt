package com.demn.plugincore

import android.content.Intent
import android.os.ParcelUuid
import android.os.Parcelable
import com.demn.plugincore.operation_result.BasicOperationResult
import com.demn.plugincore.operation_result.CommandOperationResult
import com.demn.plugincore.operation_result.OperationResult
import com.demn.plugincore.operation_result.TransitionOperationResult
import com.demn.plugincore.util.toParcelUuid
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
    val intent: Intent? = null,
    val commandName: String? = null,
    val commandUuid: ParcelUuid? = null
) : Parcelable {
    companion object {
        /**
         * Makes parcelable operation result from operation result interface
         */
        fun buildParcelableOperationResult(operationResult: OperationResult): ParcelableOperationResult {
            return when (operationResult) {
                is BasicOperationResult -> ParcelableOperationResult(
                    text = operationResult.text,
                    intent = operationResult.intent
                )

                is TransitionOperationResult -> ParcelableOperationResult(
                    initialText = operationResult.initialText,
                    initialDescription = operationResult.initialDescription,
                    finalText = operationResult.finalText,
                    finalDescription = operationResult.finalDescription
                )

                is CommandOperationResult -> ParcelableOperationResult(
                    commandUuid = operationResult.uuid.toParcelUuid(),
                    commandName = operationResult.name,
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

    if (commandUuid != null && commandName != null) { // TODO: write tests for this case
        return CommandOperationResult(
            uuid = commandUuid.uuid,
            name = commandName
        )
    }

    throw IllegalArgumentException("Incorrect ParcelableOperationResult properties")
}