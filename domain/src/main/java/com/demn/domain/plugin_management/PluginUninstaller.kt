package com.demn.domain.plugin_management

import com.demn.plugincore.Plugin

interface PluginUninstaller {
    fun uninstall(plugin: Plugin)
}