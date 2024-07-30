package com.demn.fooplugin.services

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.os.ParcelUuid
import com.demn.aidl.PluginAdapter
import com.demn.plugincore.ParcelableOperationResult
import com.demn.plugincore.ParcelablePluginCommand
import com.demn.plugincore.PluginMetadata
import com.demn.plugincore.PluginSetting
import com.demn.plugincore.operation_result.BasicOperationResult
import java.net.URLEncoder
import java.util.UUID

class BarPluginService : Service() {
    override fun onBind(intent: Intent?): IBinder {
        return addBinder()
    }

    private fun addBinder(): PluginAdapter.Stub {
        val value = object : PluginAdapter.Stub() {
            override fun executeFallbackCommand(commandUuid: ParcelUuid?, input: String?) {
                if (commandUuid?.uuid == awesomeFallbackCommandUuid) {
                    val url = "https://google.com/search?q="
                    val encodedQuery = URLEncoder.encode(input, "UTF-8")
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setData(Uri.parse("$url$encodedQuery"))
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }

                    startActivity(intent)
                }
            }

            override fun executeCommand(commandUuid: ParcelUuid?) {
                TODO("Not yet implemented")
            }

            override fun executeAnyInput(input: String?): MutableList<ParcelableOperationResult> {
                return mutableListOf()
            }

            override fun getAllCommands(): MutableList<ParcelablePluginCommand> {
                return mutableListOf(
                    ParcelablePluginCommand(
                        uuid = UUID.fromString("94244b9c-8882-49ae-a169-1c2751824dc0"),
                        name = "Search BitWarden Vault"
                    ),
                    ParcelablePluginCommand(
                        uuid = UUID.fromString("f9e4e3b1-6954-48e9-8fc6-47605468e7ac"),
                        name = "My Schedule"
                    ),
                    ParcelablePluginCommand(
                        uuid = UUID.fromString("fe6829e5-c3f5-437a-ba55-4cb052a10f34"),
                        name = "Search through all files"
                    ),
                    ParcelablePluginCommand(
                        uuid = UUID.fromString("e02d112e-96e1-44ed-aaa6-196ffa7b09d9"),
                        name = "Close all apps"
                    ),
                    ParcelablePluginCommand(
                        uuid = UUID.fromString("7638d491-a901-4355-b260-e1b0315e5212"),
                        name = "Confetti"
                    ),
                    ParcelablePluginCommand(
                        uuid = UUID.fromString("18708475-e695-43d8-9d5b-37349989a0b0"),
                        name = "Shutdown"
                    )
                )
            }

            override fun fetchPluginData(): PluginMetadata {
                return barPluginMetadata
            }

            override fun setSetting(settingUuid: ParcelUuid?, newValue: String?) {
                println("$settingUuid $newValue")
            }

            override fun getPluginSettings(): MutableList<PluginSetting> {
                return mutableListOf()
            }
        }

        return value
    }
}