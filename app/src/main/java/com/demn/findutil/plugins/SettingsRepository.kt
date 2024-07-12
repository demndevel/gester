package com.demn.findutil.plugins

import com.demn.plugincore.Plugin
import com.demn.plugincore.PluginSetting
import com.demn.plugincore.PluginSettingType
import com.demn.plugins.BuiltInPlugin
import com.demn.plugins.CorePluginsProvider
import java.util.UUID

data class PluginSettingsInfo(
    val plugin: Plugin,
    val settings: List<PluginSetting>
)

interface PluginSettingsRepository {
    suspend fun getAll(): List<PluginSettingsInfo>

    suspend fun set(pluginUuid: UUID, settingUuid: UUID, value: String)
}

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
        TODO()
        //        return empty
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
                    println("set core setting $pluginUuid $settingUuid $value")
                    corePluginsProvider.setPluginSetting(
                        // todo
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