package com.demn.fooplugin.services

import android.app.Service
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.os.ParcelUuid
import androidx.core.content.res.ResourcesCompat
import com.demn.aidl.PluginAdapter
import com.demn.fooplugin.R
import com.demn.plugincore.*
import com.demn.plugincore.ParcelableOperationResult.Companion.buildParcelableOperationResult
import com.demn.plugincore.operation_result.BasicOperationResult
import com.demn.plugincore.operation_result.IconOperationResult
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
//            override fun executeFallbackCommand(commandUuid: ParcelUuid?, input: String?) = Unit
//
//            override fun executeCommand(commandUuid: ParcelUuid?) {
//                println("foo plugin: invoked command $commandUuid")
//            }
//
//            override fun executeAnyInput(input: String?): MutableList<ParcelableOperationResult> {
//                val drawableUri = Uri.Builder()
//                    .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
//                    .authority(resources.getResourcePackageName(R.drawable.save_icon))
//                    .appendPath(resources.getResourceTypeName(R.drawable.save_icon))
//                    .appendPath(resources.getResourceEntryName(R.drawable.save_icon))
//                    .build()
//
//                println(drawableUri)
//
//                throw NotImplementedError("lol kek")
//
//                return mutableListOf(
//                    buildParcelableOperationResult(
//                        BasicOperationResult(
//                            text = "github.com",
//                            intent = getLaunchWebPageIntent("https://github.com"),
//                            type = ResultType.WebLink
//                        )
//                    ),
//                    buildParcelableOperationResult(
//                        IconOperationResult(
//                            text = "Some icon result",
//                            intent = null,
//                            iconUri = drawableUri,
//                        )
//                    )
//                )
//            }
//
//            override fun getAllCommands(): MutableList<ParcelablePluginCommand> {
//                return mutableListOf(
//                    ParcelablePluginCommand(
//                        uuid = UUID.fromString("c7b53672-d63a-400a-8148-e93ffa22d6e3"),
//                        name = "Search BitWarden vault"
//                    ),
//                    ParcelablePluginCommand(
//                        uuid = UUID.fromString("3a8680d5-853c-4e5c-aae0-84ec65b6f1d3"),
//                        name = "Look though some items"
//                    ),
//                )
//            }
//
//            override fun getPluginMetadata(): PluginMetadata {
//                return fooPluginMetadata
//            }
//
            override fun getPluginSummary(): PluginSummary {
                return fooPluginSummary
            }
//
//            override fun setSetting(settingUuid: ParcelUuid?, newValue: String?) {
//                val settingsRepository = BasicSettingsRepository(applicationContext)
//
//                println("setting the setting")
//                println(settingUuid)
//                println(newValue)
//
//                settingsRepository.write(settingUuid.toString(), newValue ?: "")
//            }
//
//            override fun getPluginSettings(): MutableList<PluginSetting> {
//                val settingsRepository = BasicSettingsRepository(applicationContext)
//
//                val fetchedSettings = settings.map {
//                    val settingValue = settingsRepository.read(it.pluginSettingUuid.toString())
//
//                    it.copy(
//                        settingValue = settingValue
//                    )
//                }
//
//                return fetchedSettings.toMutableList()
//            }
        }

        return value
    }
}

fun getLaunchWebPageIntent(url: String): Intent {
    val i = Intent(Intent.ACTION_VIEW)
    i.setData(Uri.parse(url))
    return i
}