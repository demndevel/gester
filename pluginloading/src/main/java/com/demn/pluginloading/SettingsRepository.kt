package com.demn.pluginloading

import com.demn.domain.models.ExternalPlugin
import com.demn.domain.models.PluginSettingsInfo
import com.demn.domain.plugin_management.PluginSettingsRepository
import com.demn.domain.models.Plugin
import com.demn.plugincore.parcelables.PluginSetting
import com.demn.plugincore.parcelables.PluginSettingType
import com.demn.domain.models.BuiltInPlugin
import com.demn.domain.plugin_providers.CorePluginsProvider
import com.demn.domain.plugin_providers.ExternalPluginsProvider
import java.util.UUID

class MockPluginSettingsRepository : PluginSettingsRepository {
    private val settings: MutableList<PluginSetting> = mutableListOf(
        PluginSetting(
            UUID.randomUUID(),
            UUID.randomUUID(),
            settingName = "Some setting name 1",
            settingDescription = "",
            settingValue = "some value",
            settingType = PluginSettingType.String
        ),
        PluginSetting(
            UUID.randomUUID(),
            UUID.randomUUID(),
            settingName = "Some setting name 2",
            settingDescription = "second one",
            settingValue = "1337",
            settingType = PluginSettingType.Number
        ),
        PluginSetting(
            UUID.randomUUID(),
            UUID.randomUUID(),
            settingName = "Some setting name 3",
            settingDescription = "third :-/",
            settingValue = "true",
            settingType = PluginSettingType.Boolean
        )
    )

    override suspend fun getAll(): List<PluginSettingsInfo> {
        return emptyList()
    }

    override suspend fun set(pluginUuid: UUID, settingUuid: UUID, value: String) {
        val setting = settings
            .find { it.pluginUuid == pluginUuid && it.pluginSettingUuid == settingUuid }
            ?.copy(
                settingValue = value
            )

        if (setting != null) settings[settings.indexOf(setting)] = setting
    }
}

class PluginSettingsRepositoryImpl(
    private val corePluginsProvider: CorePluginsProvider,
    private val externalPluginsProvider: ExternalPluginsProvider
) : PluginSettingsRepository {
    override suspend fun getAll(): List<PluginSettingsInfo> {
        val corePlugins = corePluginsProvider.getPlugins()
        val externalPlugins = externalPluginsProvider.getPluginList()

        val corePluginsSettingsInfos =
            corePlugins.map {
                PluginSettingsInfo(
                    it,
                    corePluginsProvider.getPluginSettings(it.metadata.pluginUuid)
                )
            }
        val externalPluginsSettingsInfos =
            externalPlugins.map {
                PluginSettingsInfo(
                    it,
                    externalPluginsProvider.getPluginSettings(it)
                )
            }

        return corePluginsSettingsInfos + externalPluginsSettingsInfos
    }

    override suspend fun set(pluginUuid: UUID, settingUuid: UUID, value: String) {
        val plugin = findPlugin(pluginUuid)

        plugin?.let {
            when (it) {
                is ExternalPlugin -> {
                    externalPluginsProvider.setPluginSetting(
                        externalPlugin = it,
                        settingUuid = settingUuid,
                        newValue = value
                    )
                }

                is BuiltInPlugin -> {
                    corePluginsProvider.setPluginSetting(
                        builtInPlugin = it,
                        settingUuid = settingUuid,
                        newValue = value
                    )
                }
            }
        }
    }

    private suspend fun findPlugin(pluginUuid: UUID): Plugin? {
        val plugins = corePluginsProvider.getPlugins() + externalPluginsProvider.getPluginList()

        return plugins.find { it.metadata.pluginUuid == pluginUuid }
    }
}