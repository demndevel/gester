package com.demn.plugincore.operation_result

/**
 * Base interface for all types of [OperationResult]
 *
 * Examples of OperationResult implementation: [BasicOperationResult] or [TransitionOperationResult]
 */
sealed interface OperationResult {
    val type: ResultType
}