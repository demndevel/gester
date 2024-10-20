package com.demn.applications_core_plugin

import android.content.Intent
import android.net.Uri
import com.demn.plugincore.operationresult.IconOperationResult
import com.demn.plugincore.operationresult.OperationResult
import com.demn.plugincore.operationresult.ResultType

internal data class ApplicationInfo(
    val name: String,
    val intent: Intent,
    val iconUri: Uri
) {
    fun toOperationResult(): OperationResult {
        return IconOperationResult(
            text = name,
            iconUri = iconUri,
            intent = intent,
            label = "Application",
            pinToTop = false
        )
    }
}