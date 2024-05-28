package com.demn.fooplugin.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.demn.aidl.IOperation
import com.demn.plugincore.ParcelableOperationResult
import com.demn.plugincore.PluginMetadata

class BarPluginService : Service() {
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
                    ParcelableOperationResult(
                        text = "bar 1 result"
                    ),
                    ParcelableOperationResult(
                        text = "bar 2 result"
                    ),
                    ParcelableOperationResult(
                        text = "bar 3 result :("
                    ),
                )
            }

            override fun executeAnyInput(input: String?): MutableList<ParcelableOperationResult> {
                return mutableListOf()
            }

            override fun fetchPluginData(): PluginMetadata {
                return barPluginMetadata
            }
        }

        return value
    }
}