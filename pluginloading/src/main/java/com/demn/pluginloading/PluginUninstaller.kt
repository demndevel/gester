package com.demn.pluginloading

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.demn.domain.models.Plugin
import com.demn.domain.pluginmanagement.PluginUninstaller

class MockPluginUninstaller : PluginUninstaller {
    override fun uninstall(plugin: Plugin) = Unit
}

class PluginUninstallerImpl(
    private val context: Context,
) : PluginUninstaller {
    override fun uninstall(plugin: Plugin) {
        if (plugin !is Plugin) return

        val intent = Intent(Intent.ACTION_DELETE).apply {
            setData(Uri.parse("package:${plugin.pluginService.packageName}"))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        context.startActivity(intent)
    }
}