package com.demn.appsearchplugin

import android.content.ContentResolver
import android.net.Uri
import com.demn.plugincore.dsl.PluginService
import com.demn.plugincore.operationresult.OperationResult
import com.demn.plugincore.parcelables.ParcelablePluginCommand
import com.demn.plugincore.parcelables.PluginVersion
import com.demn.plugincore.parcelables.buildPluginMetadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import java.util.UUID
import kotlin.coroutines.EmptyCoroutineContext

val syncAppsCacheCommandUuid: UUID = UUID.fromString("0b8dd1b6-4534-46db-b0aa-e78b467c34be")

val appSearchingPluginMetadata = buildPluginMetadata(
    pluginId = "com.demn.appsearch",
    pluginName = "App Searching Plugin"
) {
    description = "built-in plugin for plugin searching"
    version = PluginVersion(0, 1)
    consumeAnyInput = true
}

class AppSearchingPlugin : PluginService(appSearchingPluginMetadata) {
    private val applicationsRetriever: ApplicationsRetriever by inject()

    companion object {
        private val coroutineScope = CoroutineScope(EmptyCoroutineContext)
    }

    override fun onCreate() {
        super.onCreate()

        coroutineScope.launch {
            applicationsRetriever.retrieveApplications()
        }
    }

    override fun executeCommandHandler(commandUuid: UUID) {
        if (commandUuid == syncAppsCacheCommandUuid) {
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    applicationsRetriever.syncApplicationsCache()

                    applicationsRetriever.retrieveApplications()
                }
            }
        }
    }

    override fun getAllCommands(): List<ParcelablePluginCommand> {
        return listOf(
            ParcelablePluginCommand(
                uuid = syncAppsCacheCommandUuid,
                name = "Sync applications cache",
                iconUri = buildDrawableUri(R.drawable.sync_icon),
                description = null
            )
        )
    }

    override fun executeAnyInputHandler(input: String): List<OperationResult> {
        val applications = applicationsRetriever.applications.value

        return applicationsRetriever.searchApplications(
            input,
            applications
        )
    }

    private fun buildDrawableUri(resourceId: Int): Uri = Uri.Builder()
        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .authority(this.applicationContext.resources.getResourcePackageName(resourceId))
        .appendPath(this.applicationContext.resources.getResourceTypeName(resourceId))
        .appendPath(this.applicationContext.resources.getResourceEntryName(resourceId))
        .build()
}
