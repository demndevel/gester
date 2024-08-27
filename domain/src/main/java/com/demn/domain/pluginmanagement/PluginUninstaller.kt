package com.demn.domain.pluginmanagement

import com.demn.domain.models.Plugin

interface PluginUninstaller {
    fun uninstall(plugin: Plugin)
}