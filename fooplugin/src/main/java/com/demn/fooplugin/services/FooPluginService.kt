package com.demn.fooplugin.services

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.os.ParcelUuid
import com.demn.aidl.IOperation
import com.demn.plugincore.ParcelableOperationResult
import com.demn.plugincore.ParcelableOperationResult.Companion.buildParcelableOperationResult
import com.demn.plugincore.PluginMetadata
import com.demn.plugincore.PluginSetting
import com.demn.plugincore.PluginSettingType
import com.demn.plugincore.operation_result.BasicOperationResult
import com.demn.plugincore.operation_result.PriorityTag
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
        )
    )

    override fun onBind(intent: Intent?): IBinder {
        return addBinder()
    }

    private fun addBinder(): IOperation.Stub {
        val value = object : IOperation.Stub() {
            override fun executeCommand(
                commandUuid: String,
                input: String?
            ): MutableList<ParcelableOperationResult> {
                return mutableListOf(
                    buildParcelableOperationResult(
                        BasicOperationResult(
                            text = "foo 1 result"
                        )
                    ),
                    buildParcelableOperationResult(
                        BasicOperationResult(
                            text = "foo 2 result"
                        )
                    ),
                    buildParcelableOperationResult(
                        BasicOperationResult(
                            text = "foo 3 result :)"
                        ),
                    )
                )
            }

            override fun executeAnyInput(input: String?): MutableList<ParcelableOperationResult> {
                return mutableListOf(
                    buildParcelableOperationResult(
                        BasicOperationResult(
                            text = "github.com",
                            intent = getLaunchWebPageIntent("https://github.com"),
                            priority = PriorityTag.WebLink
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