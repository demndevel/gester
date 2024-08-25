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
import com.demn.coreplugins.base.CorePlugin
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.EmptyCoroutineContext

val syncAppsCacheCommandUuid = UUID.fromString("0b8dd1b6-4534-46db-b0aa-e78b467c34be")

val appSearchingMetadata = buildPluginMetadata(
    pluginUuid = UUID.fromString("57198ae0-683a-4e2a-9db4-d229707b97ce"),
    pluginName = "App Searching Plugin"
) {
    description = "built-in plugin for plugin searching"
    version = PluginVersion(0, 1)
    consumeAnyInput = true
}

class AppSearchingPlugin(
    private val context: Context,
    private val applicationsRetriever: ApplicationsRetriever,
) : CorePlugin {
    override val metadata: PluginMetadata = appSearchingMetadata
    val coroutineScope = CoroutineScope(EmptyCoroutineContext)

    private val appSearchingPluginCommands = listOf(
        PluginCommand(
            uuid = syncAppsCacheCommandUuid,
            pluginUuid = appSearchingMetadata.pluginUuid,
            name = "Sync applications cache",
            iconUri = buildDrawableUri(R.drawable.sync_icon),
            description = null
        )
    )

    private fun buildDrawableUri(resourceId: Int): Uri = Uri.Builder()
        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .authority(context.resources.getResourcePackageName(resourceId))
        .appendPath(context.resources.getResourceTypeName(resourceId))
        .appendPath(context.resources.getResourceEntryName(resourceId))
        .build()

    private var deferredApplications =
        coroutineScope.async {
            applicationsRetriever.retrieveApplications()
        }

    init {
        coroutineScope.launch {
            deferredApplications.await()
        }
    }

    override suspend fun invokeCommand(uuid: UUID) {
        if (uuid == syncAppsCacheCommandUuid) {
            withContext(Dispatchers.IO) {
                applicationsRetriever.cacheAllApplications()
                deferredApplications = async { applicationsRetriever.retrieveApplications() }
                deferredApplications.await()
            }
        }
    }

    override suspend fun getPluginCommands(): List<PluginCommand> = appSearchingPluginCommands

    override suspend fun getPluginFallbackCommands(): List<PluginFallbackCommand> = emptyList()

    override suspend fun getPluginSettings(): List<PluginSetting> {
        return emptyList()
    }

    override suspend fun invokeAnyInput(input: String): List<OperationResult> {
        return applicationsRetriever.searchApplications(
            input,
            deferredApplications.await()
        )
    }

    override suspend fun invokePluginFallbackCommand(input: String, uuid: UUID) = Unit
}