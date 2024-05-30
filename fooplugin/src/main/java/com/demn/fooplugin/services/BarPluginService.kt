package com.demn.fooplugin.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.demn.aidl.IOperation
import com.demn.plugincore.ParcelableOperationResult
import com.demn.plugincore.PluginMetadata
import com.demn.plugincore.operation_result.BasicOperationResult

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
                    ParcelableOperationResult.buildParcelableOperationResult(
                        BasicOperationResult(
                            text = "bar 2 result"
                        )
                    ),
                    ParcelableOperationResult.buildParcelableOperationResult(
                        BasicOperationResult(
                            text = "bar 3 result :("
                        )
                    )
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