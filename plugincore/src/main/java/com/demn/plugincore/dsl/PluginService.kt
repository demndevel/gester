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
import java.util.UUID

open class PluginService(private val metadata: PluginMetadata) : Service() {
    private val summary = PluginSummary(
        pluginId = metadata.pluginId,
        pluginVersion = metadata.version
    )

    final override fun onBind(intent: Intent?): IBinder {
        return addBinder()
    }

    private fun addBinder(): PluginAdapter.Stub {
        return object : PluginAdapter.Stub() {
            override fun executeFallbackCommand(commandUuid: ParcelUuid?, input: String?) {
                if (commandUuid != null && input != null) {
                    this@PluginService
                        .executeFallbackCommandHandler(commandUuid, input)
                }
            }

            override fun executeCommand(commandUuid: ParcelUuid?) {
                if (commandUuid != null)
                    this@PluginService
                        .executeCommandHandler(commandUuid.uuid)
            }

            override fun executeAnyInput(input: String?): MutableList<ParcelableOperationResult> {
                return if (input != null) {
                    this@PluginService.executeAnyInputHandler(input)
                        .map { ParcelableOperationResult.buildParcelableOperationResult(it) }
                        .toMutableList()
                } else return mutableListOf()
            }

            override fun getAllCommands(): MutableList<ParcelablePluginCommand> {
                return this@PluginService
                    .getAllCommands()
                    .toMutableList()
            }

            override fun getAllFallbackCommands(): MutableList<ParcelablePluginFallbackCommand> {
                return this@PluginService
                    .getAllFallbackCommands()
                    .toMutableList()
            }

            override fun getPluginMetadata(): PluginMetadata {
                return this@PluginService.metadata
            }

            override fun getPluginSummary(): PluginSummary = this@PluginService.summary

            override fun setSetting(settingUuid: ParcelUuid?, newValue: String?) {
                if (settingUuid != null && newValue != null)
                    this@PluginService.setSettingsHandler(settingUuid, newValue)
            }

            override fun getPluginSettings(): MutableList<PluginSetting> {
                return this@PluginService.getPluginSettings().toMutableList()
            }
        }
    }

    open fun getPluginSettings(): List<PluginSetting> = emptyList()

    open fun getAllCommands(): List<ParcelablePluginCommand> = emptyList()

    open fun getAllFallbackCommands(): List<ParcelablePluginFallbackCommand> = emptyList()

    open fun executeCommandHandler(commandUuid: UUID) = Unit

    open fun executeAnyInputHandler(input: String): List<OperationResult> = emptyList()

    open fun setSettingsHandler(settingUuid: ParcelUuid, newValue: String) = Unit

    open fun executeFallbackCommandHandler(commandUuid: ParcelUuid, input: String) = Unit
}