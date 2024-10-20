package com.demn.plugincore.operationresult

/**
 * Base interface for all types of [OperationResult]
 *
 * [pinToTop] used to top result to the top. Useful for some single information that plugin returns
 * [label] used to display label of the result. For example: application, weblink or alias
 *
 * Examples of OperationResult implementation: [BasicOperationResult] or [TransitionOperationResult]
 */
sealed interface OperationResult {
    val pinToTop: Boolean
    val label: String
}