package com.demn.plugins.core_plugins

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.demn.plugincore.PluginMetadata
import com.demn.plugincore.PluginSetting
import com.demn.plugincore.buildPluginMetadata
import com.demn.plugincore.operation_result.BasicOperationResult
import com.demn.plugincore.operation_result.OperationResult
import com.demn.plugincore.operation_result.OperationResultPriority
import com.demn.plugins.CorePlugin
import java.util.UUID

val appSearchingMetadata = buildPluginMetadata(
    pluginUuid = UUID.fromString("57198ae0-683a-4e2a-9db4-d229707b97ce"),
    pluginName = "App Searching Plugin"
) {
    description = "built-in plugin for plugin searching"
    version = "0.1"
    consumeAnyInput = true
}

class AppSearchingPlugin(
    context: Context
) : CorePlugin {
    override val metadata: PluginMetadata = appSearchingMetadata

    override fun getPluginSettings(): List<PluginSetting> {
        return emptyList()
    }

    private val packageManager = context.packageManager

    override fun invokeAnyInput(input: String): List<OperationResult> {
        val results = getAllApps()

        val filteredResults = results.filter {
            val lowercaseInput = input
                .lowercase()
                .trim()

            it.text
                .lowercase()
                .contains(lowercaseInput)
        }

        return filteredResults
    }

    private fun getAllApps(): List<BasicOperationResult> {
        val mainIntent = Intent(Intent.ACTION_MAIN)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val apps = packageManager.queryIntentActivities(mainIntent, PackageManager.MATCH_ALL)
        val results = apps.map { resolveInfo ->
            val label = resolveInfo.loadLabel(packageManager).toString()
            val intent =
                packageManager.getLaunchIntentForPackage(resolveInfo.activityInfo.packageName)

            BasicOperationResult(
                text = label,
                intent = intent,
                priority = OperationResultPriority.Application
            )
        }

        return results
    }

    override fun invokePluginCommand(input: String, uuid: UUID): List<OperationResult> {
        return emptyList()
    }
}