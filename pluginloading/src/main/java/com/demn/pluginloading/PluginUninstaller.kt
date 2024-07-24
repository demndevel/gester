package com.demn.pluginloading

import android.content.Context
import android.content.Intent
import android.net.Uri

interface PluginUninstaller {
    fun uninstall(externalPlugin: ExternalPlugin)
}

class MockPluginUninstaller : PluginUninstaller {
    override fun uninstall(externalPlugin: ExternalPlugin) = Unit
}

class PluginUninstallerImpl(
    private val context: Context,
) : PluginUninstaller {
    override fun uninstall(externalPlugin: ExternalPlugin) {
        val intent = Intent(Intent.ACTION_DELETE).apply {
            setData(Uri.parse("package:${externalPlugin.pluginService.packageName}"))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        context.startActivity(intent)
    }
}