package com.demn.fooplugin.services

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.os.ParcelUuid
import com.demn.aidl.PluginAdapter
import com.demn.plugincore.*
import com.demn.plugincore.ParcelableOperationResult.Companion.buildParcelableOperationResult
import com.demn.plugincore.operation_result.BasicOperationResult
import com.demn.plugincore.operation_result.ResultType
import java.util.UUID

class FooPluginService : Service() {
    private val settings = mutableListOf(
        PluginSetting(
            fooPluginMetadata.pluginUuid,
            UUID.fromString("e1c62eac-c1eb-415a-9e95-a3930b082cd1"),
            settingName = "Some setting",
            settingDescription = "This is a description for this example setting :)",
            settingValue = "some string setting value",
            settingType = PluginSettingType.String
        ),
        PluginSetting(
            fooPluginMetadata.pluginUuid,
            UUID.fromString("8e9c8c17-2a87-42fd-89ee-bbab755a264a"),
            settingName = "Second setting",
            settingDescription = "This is a description for this second example setting",
            settingValue = "563",
            settingType = PluginSettingType.Number
        ),
        PluginSetting(
            fooPluginMetadata.pluginUuid,
            UUID.fromString("c480c1a7-f8e0-4038-818c-aea84dff8845"),
            settingName = "Bool setting",
            settingDescription = "This is a description for this boolean example setting",
            settingValue = BooleanSettingFalse,
            settingType = PluginSettingType.Boolean
        )
    )

    override fun onBind(intent: Intent?): IBinder {
        return addBinder()
    }

    private fun addBinder(): PluginAdapter.Stub {
        val value = object : PluginAdapter.Stub() {
            override fun executeFallbackCommand(commandUuid: ParcelUuid?, input: String?) = Unit

            override fun executeAnyInput(input: String?): MutableList<ParcelableOperationResult> {
                return mutableListOf(
                    buildParcelableOperationResult(
                        BasicOperationResult(
                            text = "github.com",
                            intent = getLaunchWebPageIntent("https://github.com"),
                            type = ResultType.WebLink
                        )
                    )
                )
            }

            override fun fetchPluginData(): PluginMetadata {
                return fooPluginMetadata
            }

            override fun setSetting(settingUuid: ParcelUuid?, newValue: String?) {
                val settingsRepository = BasicSettingsRepository(applicationContext)

                println("setting the setting")
                println(settingUuid)
                println(newValue)

                settingsRepository.write(settingUuid.toString(), newValue ?: "")
            }

            override fun getPluginSettings(): MutableList<PluginSetting> {
                val settingsRepository = BasicSettingsRepository(applicationContext)

                val fetchedSettings = settings.map {
                    val settingValue = settingsRepository.read(it.pluginSettingUuid.toString())

                    it.copy(
                        settingValue = settingValue
                    )
                }

                return fetchedSettings.toMutableList()
            }
        }

        return value
    }
}

fun getLaunchWebPageIntent(url: String): Intent {
    val i = Intent(Intent.ACTION_VIEW)
    i.setData(Uri.parse(url))
    return i
}