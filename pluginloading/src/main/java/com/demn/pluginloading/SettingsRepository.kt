package com.demn.pluginloading

import com.demn.domain.models.Plugin
import com.demn.domain.models.PluginSettingsInfo
import com.demn.domain.pluginmanagement.PluginSettingsRepository
import com.demn.domain.pluginmanagement.PluginRepository
import com.demn.domain.pluginproviders.BoundServicePluginsProvider
import io.github.demndevel.gester.core.parcelables.PluginSetting
import io.github.demndevel.gester.core.parcelables.PluginSettingType
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
    private val boundServicePluginsProvider: BoundServicePluginsProvider,
    private val pluginRepository: PluginRepository
) : PluginSettingsRepository {
    override suspend fun getAll(): List<PluginSettingsInfo> {
        val externalPlugins = boundServicePluginsProvider.getPluginList()

        val externalPluginsSettingsInfos =
            externalPlugins.plugins.map {
                PluginSettingsInfo(
                    it,
                    boundServicePluginsProvider.getPluginSettings(it)
                )
            }

        return externalPluginsSettingsInfos
    }

    override suspend fun set(pluginId: String, settingUuid: UUID, value: String) {
        val plugin = findPlugin(pluginId)

        plugin?.let {
            boundServicePluginsProvider.setPluginSetting(
                plugin = it,
                settingUuid = settingUuid,
                newValue = value
            )
        }
    }

    private suspend fun findPlugin(pluginId: String): Plugin? {
        val getPluginsResult = pluginRepository.getPluginList()

        return getPluginsResult.plugins.find { it.metadata.pluginId == pluginId }
    }
}
