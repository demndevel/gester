package com.demn.plugins.core_plugins

import android.content.Context
import android.content.Intent
import com.demn.domain.models.PluginCommand
import com.demn.plugincore.PluginMetadata
import com.demn.plugincore.PluginSetting
import com.demn.plugincore.buildPluginMetadata
import com.demn.plugincore.operation_result.BasicOperationResult
import com.demn.plugincore.operation_result.OperationResult
import com.demn.plugincore.operation_result.ResultType
import com.demn.plugins.CorePlugin
import com.frosch2010.fuzzywuzzy_kotlin.FuzzySearch
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

    override fun invokeCommand(uuid: UUID) = Unit

    override fun getPluginCommands(): List<PluginCommand> = emptyList()

    private data class CachedApplicationInfo(
        val name: String,
        val intent: Intent
    ) {
        fun toOperationResult(): BasicOperationResult {
            return BasicOperationResult(
                text = name,
                intent = intent,
                type = ResultType.Application
            )
        }
    }

    private val applications by lazy {
        val mainIntent = Intent(Intent.ACTION_MAIN)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val apps = packageManager.queryIntentActivities(mainIntent, 0)

        apps.mapNotNull { resolveInfo ->
            val label = resolveInfo.loadLabel(packageManager).toString()
            val intent =
                packageManager.getLaunchIntentForPackage(resolveInfo.activityInfo.packageName)

            CachedApplicationInfo(
                name = label,
                intent = intent ?: return@mapNotNull null
            )
        }
    }

    override fun getPluginSettings(): List<PluginSetting> {
        return emptyList()
    }

    private val packageManager = context.packageManager

    override fun invokeAnyInput(input: String): List<OperationResult> {
        if (input.isBlank()) return emptyList()

        val filteredResults = applications
            .filter {
                val formattedInput = input
                    .trimIndent()
                    .replace(" ", "")
                    .lowercase()

                val formattedAppName = it.name
                    .trimIndent()
                    .replace(" ", "")
                    .lowercase()

                val ratio = FuzzySearch.tokenSetPartialRatio(formattedInput, formattedAppName)

                ratio >= 45
            }

        return filteredResults
            .map(CachedApplicationInfo::toOperationResult)
    }

    private fun getAllApps(): List<BasicOperationResult> = applications
        .map(CachedApplicationInfo::toOperationResult)

    override fun invokePluginFallbackCommand(input: String, uuid: UUID) = Unit
}