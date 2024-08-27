package com.demn.domain.models

data class GetExternalPluginListInvocationResult(
    val plugins: List<ExternalPlugin>,
    val pluginErrors: List<PluginError> = emptyList()
)