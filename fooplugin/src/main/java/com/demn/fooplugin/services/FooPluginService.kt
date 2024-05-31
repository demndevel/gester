package com.demn.fooplugin.services

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import com.demn.aidl.IOperation
import com.demn.plugincore.ParcelableOperationResult
import com.demn.plugincore.ParcelableOperationResult.Companion.buildParcelableOperationResult
import com.demn.plugincore.PluginMetadata
import com.demn.plugincore.operation_result.BasicOperationResult

class FooPluginService : Service() {
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
                    buildParcelableOperationResult(
                        BasicOperationResult(
                            text = "foo 1 result"
                        )
                    ),
                    buildParcelableOperationResult(
                        BasicOperationResult(
                            text = "foo 2 result"
                        )
                    ),
                    buildParcelableOperationResult(
                        BasicOperationResult(
                            text = "foo 3 result :)"
                        ),
                    )
                )
            }

            override fun executeAnyInput(input: String?): MutableList<ParcelableOperationResult> {
                return mutableListOf(
                    buildParcelableOperationResult(
                        BasicOperationResult(
                            text = "github.com",
                            intent = getLaunchWebPageIntent("https://github.com")
                        )
                    ),
                    buildParcelableOperationResult(
                        BasicOperationResult(
                            text = "funny result 2 found",
                        )
                    ),
                    buildParcelableOperationResult(
                        BasicOperationResult(
                            text = "funny result 3 found",
                        )
                    ),
                )
            }

            override fun fetchPluginData(): PluginMetadata {
                return fooPluginMetadata
            }
        }

        return value
    }
}

fun getLaunchWebPageIntent(url: String): Intent {
    val i = Intent(Intent.ACTION_VIEW)
    i.setData(Uri.parse(url))
    return i
}