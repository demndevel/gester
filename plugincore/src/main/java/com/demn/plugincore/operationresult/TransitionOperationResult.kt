package com.demn.plugincore.operationresult

/**
 * Operation result that shows some transition from initial state to the final state
 *
 * For example, this can be used in the currencies plugin to show transition from the USD to the EUR
 *
 * @param[initialText] initial text that should be transited
 * @param[initialDescription] description for the [initialText]
 * @param[finalText] final result of the transition of the [initialText]
 * @param[finalDescription] description for the [finalText]
 */
data class TransitionOperationResult(
    val initialText: String,
    val initialDescription: String? = null,
    val finalText: String,
    val finalDescription: String? = null
) : OperationResult {
    override val type = ResultType.Information
}