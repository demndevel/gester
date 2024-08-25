package com.demn.fooplugin.services

import android.app.Service
import android.content.ContentResolver
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.IBinder
import android.provider.Settings.*
import android.os.ParcelUuid
import com.demn.aidl.PluginAdapter
import com.demn.fooplugin.R
import com.demn.plugincore.*
import com.demn.plugincore.parcelables.ParcelableOperationResult.Companion.buildParcelableOperationResult
import com.demn.plugincore.operationresult.BasicOperationResult
import com.demn.plugincore.operationresult.IconOperationResult
import com.demn.plugincore.operationresult.ResultType
import com.demn.plugincore.parcelables.*
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

    private val openDeveloperSettingsCommandUuid = UUID.fromString("3a8680d5-853c-4e5c-aae0-84ec65b6f1d3")
    private val openManageAllApplicationsSettingsCommandUuid = UUID.fromString("e15d693c-514e-4cf9-babb-2463cb4491d3")

    override fun onBind(intent: Intent?): IBinder {
        return addBinder()
    }

    private fun addBinder(): PluginAdapter.Stub {
        val value = object : PluginAdapter.Stub() {
            override fun executeFallbackCommand(commandUuid: ParcelUuid?, input: String?) = Unit

            override fun executeCommand(commandUuid: ParcelUuid?) {
                if (commandUuid?.uuid == openDeveloperSettingsCommandUuid) {
                    val intent = Intent(ACTION_APPLICATION_DEVELOPMENT_SETTINGS).apply {
                        flags = FLAG_ACTIVITY_NEW_TASK
                    }

                    startActivity(intent)
                }

                if (commandUuid?.uuid == openManageAllApplicationsSettingsCommandUuid) {
                    val intent = Intent(ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS).apply {
                        flags = FLAG_ACTIVITY_NEW_TASK
                    }

                    startActivity(intent)
                }

                println("foo plugin: invoked command $commandUuid")
            }

            override fun executeAnyInput(input: String?): MutableList<ParcelableOperationResult> {
                val drawableUri = buildDrawableUri(R.drawable.save_icon)

                println(drawableUri)

                return mutableListOf(
                    buildParcelableOperationResult(
                        BasicOperationResult(
                            text = "github.com",
                            intent = getLaunchWebPageIntent("https://github.com"),
                            type = ResultType.WebLink
                        )
                    ),
                    buildParcelableOperationResult(
                        IconOperationResult(
                            text = "Some icon result",
                            intent = null,
                            iconUri = drawableUri,
                        )
                    )
                )
            }

            override fun getAllCommands(): MutableList<ParcelablePluginCommand> {
                return mutableListOf(
                    ParcelablePluginCommand(
                        uuid = openDeveloperSettingsCommandUuid,
                        name = "Open developer settings",
                        iconUri = buildDrawableUri(R.drawable.code_icon),
                    ),
                    ParcelablePluginCommand(
                        uuid = openManageAllApplicationsSettingsCommandUuid,
                        name = "Manage all application settings",
                        iconUri = buildDrawableUri(R.drawable.apps_icon),
                    ),
                )
            }

            override fun getAllFallbackCommands(): MutableList<ParcelablePluginFallbackCommand> = mutableListOf()

            override fun getPluginMetadata(): PluginMetadata {
                return fooPluginMetadata
            }

            override fun getPluginSummary(): PluginSummary {
                return fooPluginSummary
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

    private fun buildDrawableUri(resourceId: Int): Uri = Uri.Builder()
        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .authority(resources.getResourcePackageName(resourceId))
        .appendPath(resources.getResourceTypeName(resourceId))
        .appendPath(resources.getResourceEntryName(resourceId))
        .build()
}

fun getLaunchWebPageIntent(url: String): Intent {
    val i = Intent(Intent.ACTION_VIEW)
    i.setData(Uri.parse(url))
    return i
}