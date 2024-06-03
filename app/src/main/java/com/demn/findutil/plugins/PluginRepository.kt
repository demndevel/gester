package com.demn.findutil.plugins

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.IBinder
import com.demn.aidl.IOperation
import com.demn.plugincore.ACTION_PICK_PLUGIN
import com.demn.plugincore.CategoryExtrasKey
import com.demn.plugincore.Plugin
import com.demn.plugincore.PluginCommand
import com.demn.plugincore.PluginMetadata
import com.demn.plugincore.operation_result.OperationResult
import com.demn.plugincore.toOperationResult
import com.demn.plugins.BuiltInPlugin
import com.demn.plugins.CorePluginsProvider
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

sealed interface PluginInvocationResult<T> {
    data class Success<T>(val value: T) :
        PluginInvocationResult<T>

    class Failure<T> : PluginInvocationResult<T>
}

interface PluginRepository {
    suspend fun getPluginList(): List<Plugin>

    suspend fun invokePluginCommand(
        input: String,
        commandUuid: UUID
    ): PluginInvocationResult<List<OperationResult>>

    suspend fun getAnyResults(input: String, plugin: Plugin): List<OperationResult>

    suspend fun getAllPluginCommands(): List<PluginCommand>
}

class MockPluginRepository : PluginRepository {
    override suspend fun getPluginList(): List<Plugin> {
        return emptyList()
    }

    override suspend fun invokePluginCommand(
        input: String,
        commandUuid: UUID
    ): PluginInvocationResult<List<OperationResult>> {
        return PluginInvocationResult.Success(value = emptyList())
    }

    override suspend fun getAnyResults(input: String, plugin: Plugin): List<OperationResult> {
        return emptyList()
    }

    override suspend fun getAllPluginCommands(): List<PluginCommand> {
        return emptyList()
    }
}

class PluginRepositoryImpl(
    private val corePluginsProvider: CorePluginsProvider,
    private val context: Context
) : PluginRepository {
    @OptIn(DelicateCoroutinesApi::class)
    private val scope = GlobalScope + Dispatchers.Main

    private inner class PackageBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            scope.launch(Dispatchers.IO) {
                getPluginList()
            }
        }
    }

    private val pluginList = mutableListOf<Plugin>()

    private suspend fun fillExternalPlugins(): List<ExternalPlugin> {
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

            val metadata = fetchPluginMetadata(pluginService)

            ExternalPlugin(
                pluginService = pluginService,
                metadata = metadata
            )
        }
    }

    override suspend fun getAnyResults(input: String, plugin: Plugin): List<OperationResult> {
        return when (plugin) {
            is ExternalPlugin -> getAnyResultsWithExternalPlugin(plugin, input)

            is BuiltInPlugin -> getAnyResultsWithBuiltInPlugin(input, plugin)

            else -> emptyList()
        }
    }

    override suspend fun getAllPluginCommands(): List<PluginCommand> {
        val commands = pluginList
            .map { it.metadata.commands }
            .flatten()

        return commands
    }

    private suspend fun getAnyResultsWithExternalPlugin(
        plugin: ExternalPlugin,
        input: String
    ): List<OperationResult> {
        val intent = getIntentForPlugin(plugin.pluginService)

        return suspendCoroutine { continuation ->
            val serviceConnection = getServiceConnectionForPlugin { operation ->
                val results = operation.executeAnyInput(input).map { parcelableOperationResult ->
                    parcelableOperationResult.toOperationResult()
                }

                continuation.resume(results)
            }

            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private suspend fun getAnyResultsWithBuiltInPlugin(
        input: String,
        plugin: BuiltInPlugin,
    ): List<OperationResult> {
        val results = corePluginsProvider.invokeAnyInput(input, plugin.metadata.pluginUuid)

        return results
    }

    override suspend fun invokePluginCommand(
        input: String,
        commandUuid: UUID
    ): PluginInvocationResult<List<OperationResult>> {
        val plugin = pluginList
            .find { plugin ->
                plugin.metadata.commands.any {
                    it.id == commandUuid
                }
            }

        if (plugin == null) {
            return PluginInvocationResult.Failure()
        }

        val results = when (plugin) {
            is ExternalPlugin -> {
                invokeExternalPluginCommand(input, commandUuid, plugin)
            }

            is BuiltInPlugin -> {
                invokeBuiltInPluginCommand(input, commandUuid, plugin)
            }

            else -> emptyList()
        }

        return PluginInvocationResult.Success(results)
    }

    private suspend fun invokeExternalPluginCommand(
        input: String,
        commandUuid: UUID,
        plugin: ExternalPlugin
    ): List<OperationResult> {
        val intent = getIntentForPlugin(plugin.pluginService)

        return suspendCoroutine { continuation ->
            val serviceConnection = getServiceConnectionForPlugin { operation ->
                val results = operation.executeCommand(commandUuid.toString(), input)
                    .map { it.toOperationResult() }

                continuation.resume(results)
            }

            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private suspend fun invokeBuiltInPluginCommand(
        input: String,
        commandUuid: UUID,
        plugin: BuiltInPlugin,
    ): List<OperationResult> {
        val results = corePluginsProvider.invokePluginCommand(
            input = input,
            pluginCommandId = commandUuid,
            pluginUuid = plugin.metadata.pluginUuid
        )

        return results
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

    override suspend fun getPluginList(): List<Plugin> {
        pluginList.clear()
        val externalPlugins = fillExternalPlugins() as List<Plugin>
        val corePlugins = corePluginsProvider.getPlugins() as List<Plugin>

        pluginList.addAll(externalPlugins + corePlugins)

        return pluginList
    }

    private fun getServiceConnectionForPlugin(operationsWithPlugin: (IOperation) -> Unit) =
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                val plugin = IOperation.Stub.asInterface(service)

                operationsWithPlugin(plugin)

                context.unbindService(this)
            }

            override fun onServiceDisconnected(name: ComponentName?) = Unit
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

    private suspend fun fetchPluginMetadata(pluginService: PluginService): PluginMetadata {
        val intent = getIntentForPlugin(pluginService)

        return suspendCoroutine { continuation ->
            val serviceConnection = getServiceConnectionForPlugin { operation ->
                continuation.resume(operation.fetchPluginData())
            }

            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }
}