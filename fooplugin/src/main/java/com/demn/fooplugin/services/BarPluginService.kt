package com.demn.fooplugin.services

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.os.ParcelUuid
import com.demn.aidl.PluginAdapter
import com.demn.plugincore.ParcelableOperationResult
import com.demn.plugincore.PluginMetadata
import com.demn.plugincore.PluginSetting
import com.demn.plugincore.operation_result.BasicOperationResult
import java.net.URLEncoder

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

            override fun executeAnyInput(input: String?): MutableList<ParcelableOperationResult> {
                return mutableListOf()
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