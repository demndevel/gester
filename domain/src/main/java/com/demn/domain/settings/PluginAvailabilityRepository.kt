package com.demn.domain.settings

interface PluginAvailabilityRepository {
    fun enablePlugin(pluginId: String)

    fun disablePlugin(pluginId: String)

    fun checkPluginEnabled(pluginId: String): Boolean
}