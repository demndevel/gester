package com.demn.domain.settings

import java.util.UUID

interface PluginAvailabilityRepository {
    fun enablePlugin(pluginUuid: UUID)

    fun disablePlugin(pluginUuid: UUID)

    fun checkPluginEnabled(pluginUuid: UUID): Boolean
}