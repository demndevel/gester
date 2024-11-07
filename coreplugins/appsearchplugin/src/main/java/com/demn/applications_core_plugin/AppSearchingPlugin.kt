package com.demn.applications_core_plugin

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.demn.appsearchplugin.R
import com.demn.domain.models.PluginCommand
import com.demn.domain.models.PluginFallbackCommand
import com.demn.plugincore.parcelables.PluginMetadata
import com.demn.plugincore.parcelables.PluginSetting
import com.demn.plugincore.parcelables.PluginVersion
import com.demn.plugincore.parcelables.buildPluginMetadata
import com.demn.plugincore.operationresult.OperationResult
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.EmptyCoroutineContext

val syncAppsCacheCommandUuid: UUID = UUID.fromString("0b8dd1b6-4534-46db-b0aa-e78b467c34be")

val appSearchingMetadata = buildPluginMetadata(
    pluginId = "com.demn.appsearch",
    pluginName = "App Searching Plugin"
) {
    description = "built-in plugin for plugin searching"
    version = PluginVersion(0, 1)
    consumeAnyInput = true
}
//
//class AppSearchingPlugin(
//    private val context: Context,
//    private val applicationsRetriever: ApplicationsRetriever,
//) : CorePlugin {
//    override val metadata: PluginMetadata = appSearchingMetadata
//    private val coroutineScope = CoroutineScope(EmptyCoroutineContext)
//
//    private val appSearchingPluginCommands = listOf(
//        PluginCommand(
//            uuid = syncAppsCacheCommandUuid,
//            pluginId = appSearchingMetadata.pluginId,
//            name = "Sync applications cache",
//            iconUri = buildDrawableUri(R.drawable.sync_icon),
//            description = null
//        )
//    )
//
//    private fun buildDrawableUri(resourceId: Int): Uri = Uri.Builder()
//        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
//        .authority(context.resources.getResourcePackageName(resourceId))
//        .appendPath(context.resources.getResourceTypeName(resourceId))
//        .appendPath(context.resources.getResourceEntryName(resourceId))
//        .build()
//
//    init {
//        coroutineScope.launch {
//            applicationsRetriever.retrieveApplications()
//        }
//    }
//
//    override suspend fun invokeCommand(uuid: UUID) {
//        if (uuid == syncAppsCacheCommandUuid) {
//            withContext(Dispatchers.IO) {
//                applicationsRetriever.syncApplicationsCache()
//
//                applicationsRetriever.retrieveApplications()
//            }
//        }
//    }
//
//    override suspend fun getPluginCommands(): List<PluginCommand> = appSearchingPluginCommands
//
//    override suspend fun getPluginFallbackCommands(): List<PluginFallbackCommand> = emptyList()
//
//    override suspend fun getPluginSettings(): List<PluginSetting> {
//        return emptyList()
//    }
//
//    override suspend fun invokeAnyInput(input: String): List<OperationResult> {
//        return applicationsRetriever.searchApplications(
//            input,
//            applicationsRetriever.applications.value
//        )
//    }
//
//    override suspend fun invokePluginFallbackCommand(input: String, uuid: UUID) = Unit
//}