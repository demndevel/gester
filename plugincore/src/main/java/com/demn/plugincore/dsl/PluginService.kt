package com.demn.plugincore.dsl

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.ParcelUuid
import com.demn.aidl.PluginAdapter
import com.demn.plugincore.operationresult.OperationResult
import com.demn.plugincore.parcelables.ParcelableOperationResult
import com.demn.plugincore.parcelables.ParcelablePluginCommand
import com.demn.plugincore.parcelables.ParcelablePluginFallbackCommand
import com.demn.plugincore.parcelables.PluginMetadata
import com.demn.plugincore.parcelables.PluginSetting
import com.demn.plugincore.parcelables.PluginSummary

data class PluginSetup(
    val pluginMetadata: PluginMetadata,
    val executeFallbackCommandHandler: (commandUuid: ParcelUuid, input: String) -> Unit,
    val executeCommandHandler: (commandUuid: ParcelUuid) -> Unit,
    val executeAnyInputHandler: (input: String) -> List<OperationResult>,
    val getAllCommands: () -> List<ParcelablePluginCommand>,
    val getAllFallbackCommands: () -> List<ParcelablePluginFallbackCommand>,
    val setSettingsHandler: (settingUuid: ParcelUuid, newValue: String) -> Unit,
    val getPluginSettings: () -> List<PluginSetting>,
)

open class PluginService(private val setup: PluginSetup) : Service() {
    private val summary = PluginSummary(
        pluginId = setup.pluginMetadata.pluginId,
        pluginVersion = setup.pluginMetadata.version
    )

    override fun onBind(intent: Intent?): IBinder {
        return addBinder()
    }

    private fun addBinder(): PluginAdapter.Stub {
        return object : PluginAdapter.Stub() {
            override fun executeFallbackCommand(commandUuid: ParcelUuid?, input: String?) {
                if (commandUuid != null && input != null) {
                    setup.executeFallbackCommandHandler(commandUuid, input)
                }
            }

            override fun executeCommand(commandUuid: ParcelUuid?) {
                if (commandUuid != null) setup.executeCommandHandler(commandUuid)
            }

            override fun executeAnyInput(input: String?): MutableList<ParcelableOperationResult> {
                return if (input != null) {
                    setup.executeAnyInputHandler(input)
                        .map { ParcelableOperationResult.buildParcelableOperationResult(it) }
                        .toMutableList()
                } else return mutableListOf()
            }

            override fun getAllCommands(): MutableList<ParcelablePluginCommand> {
                return setup
                    .getAllCommands()
                    .toMutableList()
            }

            override fun getAllFallbackCommands(): MutableList<ParcelablePluginFallbackCommand> {
                return setup
                    .getAllFallbackCommands()
                    .toMutableList()
            }

            override fun getPluginMetadata(): PluginMetadata {
                return setup.pluginMetadata
            }

            override fun getPluginSummary(): PluginSummary = summary

            override fun setSetting(settingUuid: ParcelUuid?, newValue: String?) {
                if (settingUuid != null && newValue != null)
                    setup.setSettingsHandler(settingUuid, newValue)
            }

            override fun getPluginSettings(): MutableList<PluginSetting> {
                return setup.getPluginSettings().toMutableList()
            }
        }
    }
}