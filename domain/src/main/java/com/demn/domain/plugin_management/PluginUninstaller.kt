package com.demn.domain.plugin_management

import com.demn.domain.models.Plugin

interface PluginUninstaller {
    fun uninstall(plugin: Plugin)
}