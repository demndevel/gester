package com.demn.applications_core_plugin

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.demn.plugincore.operation_result.OperationResult
import com.demn.applications_core_plugin.database.ApplicationDbo
import com.demn.applications_core_plugin.database.ApplicationsDao
import com.frosch2010.fuzzywuzzy_kotlin.FuzzySearch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class ApplicationsRetriever(
    private val applicationsDao: ApplicationsDao,
    context: Context,
) {
    private val packageManager = context.packageManager

    internal suspend fun retrieveApplications(): List<ApplicationInfo> {
        return applicationsDao
            .getAll()
            .mapNotNull {
                ApplicationInfo(
                    name = it.name,
                    intent = getIntentForApp(it.packageName) ?: return@mapNotNull null,
                    iconUri = Uri.parse(it.iconUri)
                )
            }
    }

    internal fun searchApplications(
        input: String,
        applications: List<ApplicationInfo>
    ): List<OperationResult> {
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

                ratio >= 55
            }

        return filteredResults
            .map(ApplicationInfo::toOperationResult)
    }

    suspend fun cacheAllApplications() = coroutineScope {
        val mainIntent = Intent(Intent.ACTION_MAIN)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val resolveInfos = packageManager.queryIntentActivities(mainIntent, 0)

        val defers = resolveInfos.mapNotNull { resolveInfo ->
            async {
                val packageName = resolveInfo.activityInfo.packageName
                val label = resolveInfo.loadLabel(packageManager).toString()
                val iconUri = buildAppIconResourceUri(packageName, resolveInfo.iconResource)

                applicationsDao.insert(
                    ApplicationDbo(
                        packageName = packageName,
                        name = label,
                        iconUri = iconUri,
                    )
                )
            }
        }

        defers.awaitAll()
    }

    private fun getIntentForApp(packageName: String): Intent? {
        return packageManager.getLaunchIntentForPackage(packageName)
    }

    private fun buildAppIconResourceUri(packageName: String, resourceId: Int): String {
        return "android.resource://${packageName}/drawable/${resourceId}"
    }
}