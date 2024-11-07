package com.demn.plugincore.parcelables

import android.content.Intent
import android.net.Uri
import android.os.ParcelUuid
import android.os.Parcelable
import com.demn.plugincore.operationresult.BasicOperationResult
import com.demn.plugincore.operationresult.CommandOperationResult
import com.demn.plugincore.operationresult.IconOperationResult
import com.demn.plugincore.operationresult.OperationResult
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
    val label: String,
    val pinToTop: Boolean,
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
    val commandPluginId: String? = null,
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
                    label = operationResult.label,
                    pinToTop = operationResult.pinToTop
                )

                is TransitionOperationResult -> ParcelableOperationResult(
                    initialText = operationResult.initialText,
                    initialDescription = operationResult.initialDescription,
                    finalText = operationResult.finalText,
                    finalDescription = operationResult.finalDescription,
                    label = operationResult.label,
                    pinToTop = operationResult.pinToTop
                )

                is CommandOperationResult -> ParcelableOperationResult(
                    commandUuid = operationResult.uuid.toParcelUuid(),
                    commandName = operationResult.name,
                    commandPluginId = operationResult.pluginId,
                    label = operationResult.label,
                    pinToTop = operationResult.pinToTop
                )

                is IconOperationResult -> ParcelableOperationResult(
                    text = operationResult.text,
                    intent = operationResult.intent,
                    iconUri = operationResult.iconUri,
                    label = operationResult.label,
                    pinToTop = operationResult.pinToTop
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
            label = label,
            pinToTop = pinToTop
        )
    }

    if (text != null) {
        return BasicOperationResult(
            text,
            intent,
            label = label,
            pinToTop = pinToTop
        )
    }

    if (initialText != null && finalText != null) {
        return TransitionOperationResult(
            initialText,
            initialDescription,
            finalText,
            finalDescription,
            pinToTop = pinToTop
        )
    }

    if (commandUuid != null && commandName != null && commandPluginId != null && commandIconUri != null) { // TODO: write tests for this case
        return CommandOperationResult(
            uuid = commandUuid.uuid,
            pluginId = commandPluginId,
            name = commandName,
            iconUri = commandIconUri,
        )
    }

    throw IllegalArgumentException("Incorrect ParcelableOperationResult properties")
}