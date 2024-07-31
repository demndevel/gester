package com.demn.pluginwithsubmenu

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.ParcelUuid
import com.demn.aidl.PluginAdapter
import com.demn.plugincore.ParcelableOperationResult
import com.demn.plugincore.ParcelablePluginCommand
import com.demn.plugincore.PluginMetadata
import com.demn.plugincore.PluginSetting
import com.demn.plugincore.buildPluginMetadata
import java.util.UUID

class PluginWithSubmenuService : Service() {
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
                        name = "Open example plugin submenu"
                    )
                )
            }

            override fun fetchPluginData(): PluginMetadata {
                return buildPluginMetadata(
                    pluginUuid = UUID.fromString("890ec932-ea09-4713-bb2e-674fba343d3f"),
                    pluginName = "Example plugin with example submenu",
                )
            }

            override fun setSetting(settingUuid: ParcelUuid?, newValue: String?) = Unit

            override fun getPluginSettings(): MutableList<PluginSetting> {
                return mutableListOf()
            }
        }
    }
}