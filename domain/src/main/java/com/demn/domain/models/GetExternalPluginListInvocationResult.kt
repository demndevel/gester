package com.demn.domain.models

data class GetExternalPluginListInvocationResult(
    val plugins: List<Plugin>,
    val pluginErrors: List<PluginError> = emptyList()
)