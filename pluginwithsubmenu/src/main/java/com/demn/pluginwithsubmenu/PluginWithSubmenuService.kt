package com.demn.pluginwithsubmenu

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.os.ParcelUuid
import com.demn.aidl.PluginAdapter
import com.demn.plugincore.parcelables.*
import java.util.UUID

class PluginWithSubmenuService : Service() {
    private val summary = PluginSummary(
        "pluginwithsubmenu",
        PluginVersion(0, 0)
    )

    override fun onBind(intent: Intent?): IBinder {
        return addBinder()
    }

    private fun addBinder(): PluginAdapter.Stub {
        return object : PluginAdapter.Stub() {
            override fun executeFallbackCommand(commandUuid: ParcelUuid?, input: String?) = Unit

            override fun executeCommand(commandUuid: ParcelUuid?) {
                if (commandUuid?.uuid == UUID.fromString("075d1caf-2dd7-4ce3-89a3-67d6f4378935")) {
                    val intent = Intent(this@PluginWithSubmenuService, SubMenu::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(intent)
                }
            }

            override fun executeAnyInput(input: String?): MutableList<ParcelableOperationResult> {
                return mutableListOf()
            }

            override fun getAllCommands(): MutableList<ParcelablePluginCommand> {
                return mutableListOf(
                    ParcelablePluginCommand(
                        uuid = UUID.fromString("075d1caf-2dd7-4ce3-89a3-67d6f4378935"),
                        name = "Open example plugin submenu",
                        iconUri = Uri.EMPTY
                    )
                )
            }

            override fun getAllFallbackCommands(): MutableList<ParcelablePluginFallbackCommand> = mutableListOf()

            override fun getPluginMetadata(): PluginMetadata {
                return buildPluginMetadata(
                    pluginId = summary.pluginId,
                    pluginName = "Example plugin with example submenu",
                ) {
                    version = summary.pluginVersion
                }
            }

            override fun getPluginSummary(): PluginSummary {
                return summary
            }

            override fun setSetting(settingUuid: ParcelUuid?, newValue: String?) = Unit

            override fun getPluginSettings(): MutableList<PluginSetting> {
                return mutableListOf()
            }
        }
    }
}