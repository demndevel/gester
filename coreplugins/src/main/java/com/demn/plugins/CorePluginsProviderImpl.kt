package com.demn.plugins

import com.demn.plugincore.PluginSetting
import com.demn.plugincore.operation_result.OperationResult
import java.util.UUID

interface CorePluginsProvider {
    fun getPlugins(): List<BuiltInPlugin>

    suspend fun invokeAnyInput(input: String, uuid: UUID): List<OperationResult>

    suspend fun invokePluginCommand(
        input: String,
        pluginCommandId: UUID,
        pluginUuid: UUID
    ): List<OperationResult>

    fun invokePluginFallbackCommand(
        input: String,
        pluginFallbackCommandId: UUID,
        pluginUuid: UUID,
    )

    suspend fun getPluginSettings(
        pluginUuid: UUID
    ): List<PluginSetting>

    suspend fun setPluginSetting(
        builtInPlugin: BuiltInPlugin,
        settingUuid: UUID,
        newValue: String
    )
}

class CorePluginsProviderImpl(
    private val plugins: List<CorePlugin>,
    private val corePluginsSettingsRepository: CorePluginsSettingsRepository
) : CorePluginsProvider {
    override fun getPlugins(): List<BuiltInPlugin> {
        return plugins
            .map { BuiltInPlugin(it.metadata) }
    }

    override suspend fun invokeAnyInput(input: String, uuid: UUID): List<OperationResult> {
        val plugin = plugins
            .find { it.metadata.pluginUuid == uuid }

        if (plugin == null) return emptyList()

        return plugin.invokeAnyInput(input)
    }

    override suspend fun invokePluginCommand(
        input: String,
        pluginCommandId: UUID,
        pluginUuid: UUID
    ): List<OperationResult> {
        val plugin = plugins
            .find { it.metadata.pluginUuid == pluginUuid }

        if (plugin == null) {
            return emptyList()
        }

        return plugin.invokePluginCommand(input, pluginCommandId)
    }

    override fun invokePluginFallbackCommand(input: String, pluginFallbackCommandId: UUID, pluginUuid: UUID) {
        val plugin = plugins
            .find { it.metadata.pluginUuid == pluginUuid }

        if (plugin == null) return

        plugin.invokePluginFallbackCommand(input, pluginFallbackCommandId)
    }

    override suspend fun getPluginSettings(pluginUuid: UUID): List<PluginSetting> {
        val settings = plugins
            .find { it.metadata.pluginUuid == pluginUuid }
            ?.getPluginSettings()

        return settings ?: emptyList()
    }

    override suspend fun setPluginSetting(
        builtInPlugin: BuiltInPlugin,
        settingUuid: UUID,
        newValue: String
    ) {
        corePluginsSettingsRepository.set(settingUuid = settingUuid, value = newValue)
    }
}