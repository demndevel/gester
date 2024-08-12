package com.demn.pluginloading

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.demn.domain.models.ExternalPlugin
import com.demn.domain.plugin_management.PluginUninstaller
import com.demn.domain.models.Plugin

class MockPluginUninstaller : PluginUninstaller {
    override fun uninstall(plugin: Plugin) = Unit
}

class PluginUninstallerImpl(
    private val context: Context,
) : PluginUninstaller {
    override fun uninstall(plugin: Plugin) {
        if (plugin !is ExternalPlugin) return

        val intent = Intent(Intent.ACTION_DELETE).apply {
            setData(Uri.parse("package:${plugin.pluginService.packageName}"))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        context.startActivity(intent)
    }
}