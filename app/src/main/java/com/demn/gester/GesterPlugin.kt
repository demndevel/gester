package com.demn.gester

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import io.github.demndevel.gester.core.dsl.PluginService
import io.github.demndevel.gester.core.parcelables.ParcelablePluginCommand
import io.github.demndevel.gester.core.parcelables.buildPluginMetadata
import java.util.UUID

const val gesterPluginId = "com.demn.findutil.gester"

private val openSettingsCommandUuid = UUID.fromString("0292e03b-ffa7-406c-a900-78b5f860bb81")
private val helpCommandUuid = UUID.fromString("ddb2ee6b-8a35-4b56-9278-0594a8ec7b9b")
private val commandsCommandUuid = UUID.fromString("59655e19-b299-4241-af6a-0ba19e4487c1")

private val findUtilPluginMetadata =
    buildPluginMetadata(
        pluginId = gesterPluginId,
        pluginName = "findutilplugin"
    ) {
        consumeAnyInput = false
    }

class GesterPlugin : PluginService(findUtilPluginMetadata) {
    override fun executeCommandHandler(commandUuid: UUID) {
        if (commandUuid == openSettingsCommandUuid) {
            val intent = Intent(this.applicationContext, SettingsActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            this.applicationContext.startActivity(intent)
        }
    }

    override fun getAllCommands(): List<ParcelablePluginCommand> {
        return listOf(
            ParcelablePluginCommand(
                uuid = openSettingsCommandUuid,
                name = "FindUtil settings",
                iconUri = buildDrawableUri(R.drawable.settings_icon),
                description = null
            ),
        )
    }

    private fun buildDrawableUri(resourceId: Int): Uri = Uri.Builder()
        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .authority(this.applicationContext.resources.getResourcePackageName(resourceId))
        .appendPath(this.applicationContext.resources.getResourceTypeName(resourceId))
        .appendPath(this.applicationContext.resources.getResourceEntryName(resourceId))
        .build()
}
