package com.demn.appsearchplugin

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.demn.plugincore.operationresult.OperationResult
import com.demn.appsearchplugin.database.ApplicationDbo
import com.demn.appsearchplugin.database.ApplicationsDao
import com.demn.domain.util.cyrillicToLatin
import com.frosch2010.fuzzywuzzy_kotlin.FuzzySearch
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class ApplicationsRetriever(
    private val applicationsDao: ApplicationsDao,
    context: Context,
) {
    private val packageManager = context.packageManager

    private val _applications = MutableStateFlow(listOf<ApplicationInfo>())
    internal val applications: StateFlow<List<ApplicationInfo>> = _applications

    internal suspend fun retrieveApplications() = withContext(Dispatchers.IO) {
        _applications.update {
            applicationsDao
                .getAll()
                .mapNotNull {
                    ApplicationInfo(
                        name = it.name,
                        intent = getIntentForApp(it.packageName) ?: return@mapNotNull null,
                        iconUri = Uri.parse(it.iconUri)
                    )
                }
        }
    }

    internal fun searchApplications(
        input: String,
        applications: List<ApplicationInfo>
    ): List<OperationResult> {
        if (input.isBlank()) return emptyList()

        val filteredResults = applications
            .filter { application ->
                val formattedInput = input
                    .trimIndent()
                    .replace(" ", "")
                    .lowercase()
                    .let { cyrillicToLatin(it) }

                val formattedAppName = application.name
                    .trimIndent()
                    .replace(" ", "")
                    .lowercase()
                    .let { cyrillicToLatin(it) }

                val ratio = FuzzySearch.tokenSetPartialRatio(formattedInput, formattedAppName)

                ratio >= 65
            }

        return filteredResults
            .map(ApplicationInfo::toOperationResult)
    }

    suspend fun syncApplicationsCache() = coroutineScope {
        val cachedApplications = applicationsDao.getAll()

        val mainIntent = Intent(Intent.ACTION_MAIN)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val resolveInfos = packageManager.queryIntentActivities(mainIntent, 0)

        val defers = resolveInfos.mapNotNull { resolveInfo ->
            val alreadyCached =
                cachedApplications.any { cachedApp -> cachedApp.packageName == resolveInfo.activityInfo.packageName }

            if (alreadyCached) return@mapNotNull null

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