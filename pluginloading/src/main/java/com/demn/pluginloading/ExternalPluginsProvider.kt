package com.demn.pluginloading

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.IBinder
import com.demn.aidl.PluginAdapter
import com.demn.domain.data.PluginCommandCacheRepository
import com.demn.domain.models.ExternalPlugin
import com.demn.domain.models.PluginCommand
import com.demn.domain.models.PluginService
import com.demn.domain.plugin_providers.ExternalPluginsProvider
import com.demn.plugincore.ACTION_PICK_PLUGIN
import com.demn.plugincore.CategoryExtrasKey
import com.demn.plugincore.PluginMetadata
import com.demn.plugincore.PluginSetting
import com.demn.plugincore.util.toParcelUuid
import com.demn.plugincore.operation_result.OperationResult
import com.demn.plugincore.toOperationResult
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ExternalPluginsProviderImpl(
    private val context: Context,
    private val pluginCommandCacheRepository: PluginCommandCacheRepository
) : ExternalPluginsProvider {
    private inner class PackageBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) = Unit
    }

    override suspend fun getPluginList(): List<ExternalPlugin> {
        val packageManager = context.packageManager
        val baseIntent = Intent(ACTION_PICK_PLUGIN).apply {
            flags = Intent.FLAG_DEBUG_LOG_RESOLUTION
        }
        val list =
            packageManager.queryIntentServices(baseIntent, PackageManager.GET_RESOLVED_FILTER)

        return list.map { resolveInfo ->
            val serviceInfo = resolveInfo.serviceInfo

            val pluginService = PluginService(
                packageName = serviceInfo.packageName,
                serviceName = serviceInfo.name,
                actions = getActions(resolveInfo),
                categories = getCategories(resolveInfo)
            )

            ExternalPlugin(
                pluginService = pluginService,
                metadata = getPluginData(pluginService)
            )
        }
    }

    private fun getCategories(resolveInfo: ResolveInfo): List<String> {
        val categories = mutableListOf<String>()
        for (i in 0 until resolveInfo.filter.countCategories()) {
            categories.add(resolveInfo.filter.getCategory(i))
        }
        return categories
    }

    private fun getActions(resolveInfo: ResolveInfo): List<String> {
        val actions = mutableListOf<String>()
        for (i in 0 until resolveInfo.filter.countActions()) {
            actions.add(resolveInfo.filter.getAction(i))
        }
        return actions
    }

    override suspend fun executeFallbackCommand(
        input: String,
        fallbackCommandUuid: UUID,
        pluginService: PluginService
    ) {
        val intent = getIntentForPlugin(pluginService)

        val serviceConnection = getServiceConnectionForPlugin { adapter ->
            adapter.executeFallbackCommand(fallbackCommandUuid.toParcelUuid(), input)
        }

        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private suspend fun getPluginsForCacheUpdate(plugins: List<ExternalPlugin>): List<ExternalPlugin> {
        val pluginsCache = pluginCommandCacheRepository.getAllPlugins()

        return plugins.mapNotNull { plugin ->
            val pluginCache = pluginsCache
                .find { it.pluginUuid == plugin.metadata.pluginUuid }

            if (pluginCache == null) return@mapNotNull plugin

            if (pluginCache.version != plugin.metadata.version) return@mapNotNull plugin

            return@mapNotNull null
        }
    }

    private suspend fun updatePluginsCache(plugins: List<ExternalPlugin>) {
        plugins.forEach { plugin ->
            val commands = getPluginCommandsDirectly(plugin)
            pluginCommandCacheRepository.updatePluginCache(
                plugin.toPluginCache(commands)
            )
        }
    }

    private suspend fun updatePluginCacheIfDeprecated(plugin: ExternalPlugin) {
        val pluginsCache = pluginCommandCacheRepository.getAllPlugins()
        val pluginCache = pluginsCache.find { it.pluginUuid == plugin.metadata.pluginUuid }

        if (pluginCache?.version == plugin.metadata.version) return
        if (pluginCache != null) return

        val commands = getPluginCommandsDirectly(plugin)
        pluginCommandCacheRepository.updatePluginCache(
            plugin.toPluginCache(commands)
        )
    }

    override suspend fun getPluginCommandsDirectly(plugin: ExternalPlugin): List<PluginCommand> {
        val intent = getIntentForPlugin(plugin.pluginService)

        return suspendCoroutine { continuation ->
            val serviceConnection = getServiceConnectionForPlugin { adapter ->
                val commands = adapter.allCommands
                    .map { it.toPluginCommand(plugin.metadata.pluginUuid) }

                continuation.resume(commands)
            }

            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override suspend fun getPluginCommands(plugin: ExternalPlugin): List<PluginCommand> {
        updatePluginCacheIfDeprecated(plugin)

        return pluginCommandCacheRepository.getAllPlugins()
            .find { it.pluginUuid == plugin.metadata.pluginUuid }
            ?.commands
            ?: emptyList()
    }

    override suspend fun getPluginCommands(): List<PluginCommand> {
        val plugins = getPluginList()
        val pluginsForCacheUpdate = getPluginsForCacheUpdate(plugins)
        updatePluginsCache(pluginsForCacheUpdate)

        return pluginCommandCacheRepository.getAllPlugins()
            .flatMap { it.commands }
    }

    override suspend fun executeCommand(uuid: UUID, pluginUuid: UUID) {
        val plugin = getPluginList()
            .find { it.metadata.pluginUuid == pluginUuid }

        if (plugin == null) return

        val intent = getIntentForPlugin(plugin.pluginService)

        val serviceConnection = getServiceConnectionForPlugin { adapter ->
            adapter.executeCommand(uuid.toParcelUuid())
        }

        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun getIntentForPlugin(plugin: PluginService): Intent {
        return Intent(ACTION_PICK_PLUGIN).apply {
            setClassName(
                plugin.packageName,
                plugin.serviceName
            )

            putExtra(CategoryExtrasKey, plugin.categories.first())
        }
    }

    override suspend fun executeAnyInput(
        input: String,
        pluginService: PluginService
    ): List<OperationResult> {
        val intent = getIntentForPlugin(pluginService)

        return suspendCoroutine { continuation ->
            val serviceConnection = getServiceConnectionForPlugin { adapter ->
                val results = adapter.executeAnyInput(input).map { parcelableOperationResult ->
                    parcelableOperationResult.toOperationResult()
                }

                continuation.resume(results)
            }

            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun getServiceConnectionForPlugin(operationsWithPlugin: (PluginAdapter) -> Unit) =
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                val plugin = PluginAdapter.Stub.asInterface(service)

                operationsWithPlugin(plugin)

                context.unbindService(this)
            }

            override fun onServiceDisconnected(name: ComponentName?) = Unit
        }

    override suspend fun getPluginData(pluginService: PluginService): PluginMetadata {
        val intent = getIntentForPlugin(pluginService)

        return suspendCoroutine { continuation ->
            val serviceConnection = getServiceConnectionForPlugin { adapter ->
                continuation.resume(adapter.fetchPluginData())
            }

            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override suspend fun getPluginSettings(externalPlugin: ExternalPlugin): List<PluginSetting> {
        val intent = getIntentForPlugin(externalPlugin.pluginService)

        return suspendCoroutine { continuation ->
            val serviceConnection = getServiceConnectionForPlugin { adapter ->
                continuation.resume(adapter.pluginSettings)
            }

            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override suspend fun setPluginSetting(
        externalPlugin: ExternalPlugin,
        settingUuid: UUID,
        newValue: String
    ) {
        val intent = getIntentForPlugin(externalPlugin.pluginService)

        return suspendCoroutine { continuation ->
            val serviceConnection = getServiceConnectionForPlugin { adapter ->
                continuation.resume(
                    adapter.setSetting(
                        settingUuid.toParcelUuid(),
                        newValue
                    )
                )
            }

            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }
}