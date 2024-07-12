package com.demn.pluginloading

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.IBinder
import android.os.ParcelUuid
import com.demn.aidl.IOperation
import com.demn.plugincore.ACTION_PICK_PLUGIN
import com.demn.plugincore.CategoryExtrasKey
import com.demn.plugincore.PluginMetadata
import com.demn.plugincore.PluginSetting
import com.demn.plugincore.operation_result.OperationResult
import com.demn.plugincore.toOperationResult
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface ExternalPluginsProvider {
    suspend fun getPluginList(): List<ExternalPlugin>

    suspend fun executeCommand(
        input: String,
        commandUuid: UUID,
        pluginService: PluginService
    ): List<OperationResult>

    suspend fun executeAnyInput(
        input: String,
        pluginService: PluginService
    ): List<OperationResult>

    suspend fun getPluginData(pluginService: PluginService): PluginMetadata

    suspend fun getPluginSettings(externalPlugin: ExternalPlugin): List<PluginSetting>

    suspend fun setPluginSetting(
        externalPlugin: ExternalPlugin,
        settingUuid: UUID,
        newValue: String
    )
}

class ExternalPluginsProviderImpl(
    private val context: Context
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

    override suspend fun executeCommand(
        input: String,
        commandUuid: UUID,
        pluginService: PluginService
    ): List<OperationResult> {
        val intent = getIntentForPlugin(pluginService)

        return suspendCoroutine { continuation ->
            val serviceConnection = getServiceConnectionForPlugin { operation ->
                val results = operation.executeCommand(commandUuid.toString(), input)
                    .map { it.toOperationResult() }

                continuation.resume(results)
            }

            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
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
            val serviceConnection = getServiceConnectionForPlugin { operation ->
                val results = operation.executeAnyInput(input).map { parcelableOperationResult ->
                    parcelableOperationResult.toOperationResult()
                }

                continuation.resume(results)
            }

            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
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

    override suspend fun getPluginData(pluginService: PluginService): PluginMetadata {
        val intent = getIntentForPlugin(pluginService)

        return suspendCoroutine { continuation ->
            val serviceConnection = getServiceConnectionForPlugin { operation ->
                continuation.resume(operation.fetchPluginData())
            }

            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override suspend fun getPluginSettings(externalPlugin: ExternalPlugin): List<PluginSetting> {
        val intent = getIntentForPlugin(externalPlugin.pluginService)

        return suspendCoroutine { continuation ->
            val serviceConnection = getServiceConnectionForPlugin { operation ->
                continuation.resume(operation.pluginSettings)
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
            val serviceConnection = getServiceConnectionForPlugin { operation ->
                continuation.resume(
                    operation.setSetting(
                        ParcelUuid.fromString(settingUuid.toString()),
                        newValue
                    )
                )
            }

            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }
}