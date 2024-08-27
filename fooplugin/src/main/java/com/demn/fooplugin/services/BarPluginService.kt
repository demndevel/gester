package com.demn.fooplugin.services

import android.app.Service
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.os.ParcelUuid
import com.demn.aidl.PluginAdapter
import com.demn.fooplugin.R
import com.demn.plugincore.operationresult.TransitionOperationResult
import com.demn.plugincore.parcelables.*
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

            override fun executeCommand(commandUuid: ParcelUuid?) {
                println("bar plugin: invoked command $commandUuid")
            }

            override fun executeAnyInput(input: String?): MutableList<ParcelableOperationResult> {
                println(input)
                return if (input == "5$") {
                    mutableListOf(
                        ParcelableOperationResult.buildParcelableOperationResult(
                            TransitionOperationResult(
                                initialText = "5$",
                                initialDescription = "American Dollars USD",
                                finalText = "500RUB",
                                finalDescription = "Russian Roubles RUB"
                            )
                        )
                    )
                } else mutableListOf()
            }

            override fun getAllCommands(): MutableList<ParcelablePluginCommand> {
                return mutableListOf()
            }

            override fun getAllFallbackCommands(): MutableList<ParcelablePluginFallbackCommand> {
                return mutableListOf(
                    ParcelablePluginFallbackCommand(
                        uuid = awesomeFallbackCommandUuid,
                        name = "Search Google",
                        iconUri = buildDrawableUri(R.drawable.travel_explore_icon),
                        description = "Opens google search page with default browser app"
                    )
                )
            }

            override fun getPluginMetadata(): PluginMetadata {
                return barPluginMetadata
            }

            override fun getPluginSummary(): PluginSummary {
                return barPluginSummary
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

    private fun buildDrawableUri(resourceId: Int): Uri = Uri.Builder()
        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .authority(resources.getResourcePackageName(resourceId))
        .appendPath(resources.getResourceTypeName(resourceId))
        .appendPath(resources.getResourceEntryName(resourceId))
        .build()
}