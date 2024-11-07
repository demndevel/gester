package com.demn.coreplugins.base

import com.demn.domain.models.BuiltInPlugin
import com.demn.domain.models.PluginCommand
import com.demn.domain.models.PluginFallbackCommand
import com.demn.domain.pluginproviders.CorePluginsProvider
import com.demn.plugincore.parcelables.PluginSetting
import com.demn.plugincore.operationresult.OperationResult
import java.util.UUID

class CorePluginsProviderImpl(
    private val plugins: List<CorePlugin>,
    private val corePluginsSettingsRepository: CorePluginsSettingsRepository
) : CorePluginsProvider {
    override fun getPlugins(): List<BuiltInPlugin> {
        return plugins
            .map { BuiltInPlugin(it.metadata) }
    }

    override suspend fun invokeAnyInput(input: String, pluginId: String): List<OperationResult> {
        val plugin = plugins
            .find { it.metadata.pluginId == pluginId }

        if (plugin == null) return emptyList()

        return plugin.invokeAnyInput(input)
    }

    override suspend fun getPluginCommands(plugin: BuiltInPlugin): List<PluginCommand> {
        plugins
            .find { it.metadata.pluginId == plugin.metadata.pluginId }
            ?.let {
                return it.getPluginCommands()
            }

        return emptyList()
    }

    override suspend fun getAllPluginCommands(): List<PluginCommand> {
        return plugins.flatMap { it.getPluginCommands() }
    }

    override suspend fun getAllPluginFallbackCommands(): List<PluginFallbackCommand> {
        return plugins.flatMap { it.getPluginFallbackCommands() }
    }

    override suspend fun invokePluginCommand(commandUuid: UUID, pluginId: String) {
        plugins
            .find { it.metadata.pluginId == pluginId }
            ?.invokeCommand(commandUuid)
    }

    override suspend fun invokePluginFallbackCommand(
        input: String,
        pluginFallbackCommandId: UUID,
        pluginId: String
    ) {
        val plugin = plugins
            .find { it.metadata.pluginId == pluginId }

        if (plugin == null) return

        plugin.invokePluginFallbackCommand(input, pluginFallbackCommandId)
    }

    override suspend fun getPluginSettings(pluginUuid: String): List<PluginSetting> {
        val settings = plugins
            .find { it.metadata.pluginId == pluginUuid }
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