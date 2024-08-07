package com.demn.domain.models

sealed interface PluginInvocationResult {
    data object Success : PluginInvocationResult

    data object Failure : PluginInvocationResult
}