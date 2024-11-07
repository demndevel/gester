package com.demn.domain.models

data class GetBoundServicePluginListInvocationResult(
    val plugins: List<Plugin>,
    val pluginErrors: List<PluginError> = emptyList()
)