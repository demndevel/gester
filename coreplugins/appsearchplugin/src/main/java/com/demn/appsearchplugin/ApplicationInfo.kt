package com.demn.appsearchplugin

import android.content.Intent
import android.net.Uri
import io.github.demndevel.gester.core.operationresult.IconOperationResult
import io.github.demndevel.gester.core.operationresult.OperationResult

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
