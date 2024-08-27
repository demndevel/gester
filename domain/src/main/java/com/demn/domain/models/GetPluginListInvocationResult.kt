package com.demn.domain.models

data class GetPluginListInvocationResult(
    val plugins: List<Plugin>,
    val pluginErrors: List<PluginError> = emptyList()
)
