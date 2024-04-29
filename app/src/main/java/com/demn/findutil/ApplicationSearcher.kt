package com.demn.findutil

import android.content.Context
import android.content.pm.PackageManager
import com.demn.findutil.models.Application

class ApplicationSearcher(
    context: Context
) {
    private val packageManager: PackageManager = context.packageManager

    fun find(searchQuery: String): List<Application> {
        val allPackages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val foundPackages = allPackages
            .mapNotNull {
                if (it.name == null) return@mapNotNull null
                if (it.name.contains(searchQuery)) return@mapNotNull it
                return@mapNotNull null
            }

        val applications = foundPackages
            .map { applicationInfo ->
                Application(
                    name = applicationInfo.name,
                    intentToLaunch = packageManager.getLaunchIntentForPackage(applicationInfo.packageName)
                )
            }

        return applications
    }
}