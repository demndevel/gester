package com.demn.pluginloading

import com.demn.domain.models.ExternalPlugin
import com.demn.domain.models.PluginSettingsInfo
import com.demn.domain.pluginmanagement.PluginSettingsRepository
import com.demn.domain.models.Plugin
import com.demn.plugincore.parcelables.PluginSetting
import com.demn.plugincore.parcelables.PluginSettingType
import com.demn.domain.models.BuiltInPlugin
import com.demn.domain.pluginmanagement.PluginRepository
import com.demn.domain.pluginproviders.CorePluginsProvider
import com.demn.domain.pluginproviders.ExternalPluginsProvider
import java.util.UUID

class MockPluginSettingsRepository : PluginSettingsRepository {
    private val settings: MutableList<PluginSetting> = mutableListOf(
        PluginSetting(
            pluginId = "test1",
            UUID.randomUUID(),
            settingName = "Some setting name 1",
            settingDescription = "",
            settingValue = "some value",
            settingType = PluginSettingType.String
        ),
        PluginSetting(
            pluginId = "test2",
            UUID.randomUUID(),
            settingName = "Some setting name 2",
            settingDescription = "second one",
            settingValue = "1337",
            settingType = PluginSettingType.Number
        ),
        PluginSetting(
            pluginId = "test3",
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

    override suspend fun set(pluginId: String, settingUuid: UUID, value: String) {
        val setting = settings
            .find { it.pluginId == pluginId && it.pluginSettingUuid == settingUuid }
            ?.copy(
                settingValue = value
            )

        if (setting != null) settings[settings.indexOf(setting)] = setting
    }
}

class PluginSettingsRepositoryImpl(
    private val corePluginsProvider: CorePluginsProvider,
    private val externalPluginsProvider: ExternalPluginsProvider,
    private val pluginRepository: PluginRepository
) : PluginSettingsRepository {
    override suspend fun getAll(): List<PluginSettingsInfo> {
        val corePlugins = corePluginsProvider.getPlugins()
        val externalPlugins = externalPluginsProvider.getPluginList()

        val corePluginsSettingsInfos =
            corePlugins.map {
                PluginSettingsInfo(
                    it,
                    corePluginsProvider.getPluginSettings(it.metadata.pluginId)
                )
            }
        val externalPluginsSettingsInfos =
            externalPlugins.plugins.map {
                PluginSettingsInfo(
                    it,
                    externalPluginsProvider.getPluginSettings(it)
                )
            }

        return corePluginsSettingsInfos + externalPluginsSettingsInfos
    }

    override suspend fun set(pluginId: String, settingUuid: UUID, value: String) {
        val plugin = findPlugin(pluginId)

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

    private suspend fun findPlugin(pluginId: String): Plugin? {
        val getPluginsResult = pluginRepository.getPluginList()

        return getPluginsResult.plugins.find { it.metadata.pluginId == pluginId }
    }
}