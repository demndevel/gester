package com.demn.applications_core_plugin

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.demn.plugincore.operation_result.OperationResult
import com.demn.applications_core_plugin.database.ApplicationDbo
import com.demn.applications_core_plugin.database.ApplicationsDao
import com.frosch2010.fuzzywuzzy_kotlin.FuzzySearch
import com.michaeltroger.latintocyrillic.Alphabet
import com.michaeltroger.latintocyrillic.LatinCyrillicFactory
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

    internal suspend fun searchApplications(
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
                    .let { convertCyrillicToLatin(it) }

                val formattedAppName = application.name
                    .trimIndent()
                    .replace(" ", "")
                    .lowercase()
                    .let { convertCyrillicToLatin(it) }

                val ratio = FuzzySearch.tokenSetPartialRatio(formattedInput, formattedAppName)

                ratio >= 65
            }

        return filteredResults
            .map(ApplicationInfo::toOperationResult)
    }

    private suspend fun convertCyrillicToLatin(
        input: String
    ): String {
        val latinCyrillic = LatinCyrillicFactory.create(Alphabet.RussianIso9)

        return if (latinCyrillic.isCyrillic(input)) latinCyrillic.cyrillicToLatin(input) else input
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