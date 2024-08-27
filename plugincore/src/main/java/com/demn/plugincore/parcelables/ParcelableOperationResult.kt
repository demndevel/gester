package com.demn.plugincore.parcelables

import android.content.Intent
import android.net.Uri
import android.os.ParcelUuid
import android.os.Parcelable
import com.demn.plugincore.operationresult.BasicOperationResult
import com.demn.plugincore.operationresult.CommandOperationResult
import com.demn.plugincore.operationresult.IconOperationResult
import com.demn.plugincore.operationresult.OperationResult
import com.demn.plugincore.operationresult.ResultType
import com.demn.plugincore.operationresult.TransitionOperationResult
import com.demn.plugincore.util.toParcelUuid
import kotlinx.parcelize.Parcelize

/**
 * There should be set either text or initial&final texts.
 *
 * This is a parcelable implementation of the [OperationResult]. Parcelable used because inheritance is too hard to implement in Parcelable API
 */
@Parcelize
class ParcelableOperationResult private constructor(
    val resultType: ResultType,
    val text: String? = null,
    val description: String? = null,
    val iconUri: Uri? = null,
    val initialText: String? = null,
    val initialDescription: String? = null,
    val finalText: String? = null,
    val finalDescription: String? = null,
    val intent: Intent? = null,
    val commandName: String? = null,
    val commandIconUri: Uri? = null,
    val commandUuid: ParcelUuid? = null,
    val commandPluginUuid: ParcelUuid? = null,
) : Parcelable {
    companion object {
        /**
         * Makes parcelable operation result from operation result interface
         */
        fun buildParcelableOperationResult(operationResult: OperationResult): ParcelableOperationResult {
            return when (operationResult) {
                is BasicOperationResult -> ParcelableOperationResult(
                    text = operationResult.text,
                    intent = operationResult.intent,
                    resultType = operationResult.type
                )

                is TransitionOperationResult -> ParcelableOperationResult(
                    initialText = operationResult.initialText,
                    initialDescription = operationResult.initialDescription,
                    finalText = operationResult.finalText,
                    finalDescription = operationResult.finalDescription,
                    resultType = operationResult.type
                )

                is CommandOperationResult -> ParcelableOperationResult(
                    commandUuid = operationResult.uuid.toParcelUuid(),
                    commandName = operationResult.name,
                    commandPluginUuid = operationResult.pluginUuid.toParcelUuid(),
                    resultType = operationResult.type
                )

                is IconOperationResult -> ParcelableOperationResult(
                    text = operationResult.text,
                    intent = operationResult.intent,
                    iconUri = operationResult.iconUri,
                    resultType = operationResult.type
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
    if (text != null && iconUri != null) {
        return IconOperationResult(
            text = text,
            intent = intent,
            iconUri = iconUri,
            resultType
        )
    }

    if (text != null) {
        return BasicOperationResult(
            text,
            intent,
            resultType
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

    if (commandUuid != null && commandName != null && commandPluginUuid != null && commandIconUri != null) { // TODO: write tests for this case
        return CommandOperationResult(
            uuid = commandUuid.uuid,
            pluginUuid = commandPluginUuid.uuid,
            name = commandName,
            iconUri = commandIconUri
        )
    }

    throw IllegalArgumentException("Incorrect ParcelableOperationResult properties")
}