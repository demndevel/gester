package com.demn.pluginloading

import android.content.*
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.DeadObjectException
import android.os.IBinder
import android.os.RemoteException
import com.demn.aidl.PluginAdapter
import com.demn.domain.data.PluginCacheRepository
import com.demn.domain.data.PluginCache
import com.demn.domain.models.*
import com.demn.domain.pluginproviders.BoundServicePluginsProvider
import com.demn.plugincore.*
import com.demn.plugincore.operationresult.OperationResult
import com.demn.plugincore.parcelables.*
import com.demn.plugincore.util.toParcelUuid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BoundServicePluginsProviderImpl(
    private val context: Context,
    private val pluginCacheRepository: PluginCacheRepository
) : BoundServicePluginsProvider {
    override suspend fun getPluginList(): GetBoundServicePluginListInvocationResult {
        val packageManager = context.packageManager
        val baseIntent = Intent(ACTION_PICK_PLUGIN).apply {
            flags = Intent.FLAG_DEBUG_LOG_RESOLUTION
        }
        val list =
            packageManager.queryIntentServices(baseIntent, PackageManager.GET_RESOLVED_FILTER)
        val pluginErrors = mutableListOf<PluginError>()

        val pluginList = list.mapNotNull { resolveInfo ->
            val serviceInfo = resolveInfo.serviceInfo
            val pluginService = PluginService(
                packageName = serviceInfo.packageName,
                serviceName = serviceInfo.name,
                actions = getActions(resolveInfo),
                categories = getCategories(resolveInfo)
            )

            try {
                val pluginSummaryResult = getPluginSummary(pluginService)
                val pluginSummary = pluginSummaryResult.getOrElse { ex ->
                    pluginErrors += PluginError(
                        pluginId = null,
                        pluginName = null,
                        type = PluginErrorType.Unloaded,
                        message = """
                            exception: ${ex.message}
                            plugin service: $pluginService
                        """.trimIndent()
                    )
                    return@mapNotNull null
                }

                cacheIfRequired(pluginSummary, pluginService)

                val pluginCache = pluginCacheRepository.getPluginCache(pluginSummary.pluginId)
                val metadata = pluginCache?.pluginMetadata ?: return@mapNotNull null

                Plugin(
                    pluginService = pluginService,
                    metadata = metadata
                )
            } catch (ex: IllegalStateException) {
                pluginErrors += PluginError(
                    pluginId = null,
                    pluginName = null,
                    type = PluginErrorType.Unloaded,
                    message = """
                        plugin service: $pluginService
                        exception: ${ex.message}
                    """.trimIndent()
                )

                return@mapNotNull null
            }
        }

        return GetBoundServicePluginListInvocationResult(pluginList, pluginErrors)
    }

    private suspend fun cacheIfRequired(pluginSummary: PluginSummary, pluginService: PluginService) {
        val cache = pluginCacheRepository.getPluginCache(pluginSummary.pluginId)

        if (cache?.pluginMetadata?.version == pluginSummary.pluginVersion) return

        pluginCacheRepository.updatePluginCache(
            PluginCache(
                getPluginMetadataIpc(pluginService),
                getPluginCommandsIpc(pluginSummary.pluginId, pluginService),
                getPluginFallbackCommandsIpc(pluginSummary.pluginId, pluginService),
            )
        )
    }

    private suspend fun getPluginFallbackCommandsIpc(
        pluginId: String,
        pluginService: PluginService
    ): List<PluginFallbackCommand> = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            performOperationsWithPlugin(pluginService) { adapter ->
                val fallbackCommands = adapter
                    .getAllFallbackCommands()
                    .map { it.toPluginFallbackCommand(pluginId) }

                continuation.resume(fallbackCommands)
            }
        }
    }

    override suspend fun executeFallbackCommand(
        input: String,
        fallbackCommandUuid: UUID,
        pluginService: PluginService
    ) = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            performOperationsWithPlugin(pluginService) { adapter ->
                continuation.resume(adapter.executeFallbackCommand(fallbackCommandUuid.toParcelUuid(), input))
            }
        }
    }

    override suspend fun getAllPluginCommands(): List<PluginCommand> {
        val plugins = pluginCacheRepository.getAllPlugins()

        return plugins
            .flatMap {
                it.commands
            }
    }

    override suspend fun getAllPluginFallbackCommands(): List<PluginFallbackCommand> {
        val plugins = pluginCacheRepository.getAllPlugins()

        return plugins
            .flatMap {
                it.fallbackCommands
            }
    }

    override suspend fun executeCommand(uuid: UUID, pluginId: String) = withContext(Dispatchers.IO) {
        val plugin = getPluginList().plugins
            .find { it.metadata.pluginId == pluginId }

        if (plugin == null) return@withContext

        suspendCoroutine { continuation ->
            performOperationsWithPlugin(plugin.pluginService) { pluginAdapter ->
                continuation.resume(
                    pluginAdapter
                        .executeCommand(uuid.toParcelUuid())
                )
            }
        }
    }

    override suspend fun executeAnyInput(
        input: String,
        pluginService: PluginService,
    ): List<OperationResult> = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            performOperationsWithPlugin(
                pluginService = pluginService,
                onResult = { result ->
                    if (result is PluginInvocationResult.Failure) {
                        continuation.resume(emptyList())
                    }
                }
            ) { pluginAdapter ->
                continuation.resume(
                    pluginAdapter
                        .executeAnyInput(input)
                        .map(ParcelableOperationResult::toOperationResult)
                )
            }
        }
    }

    override suspend fun getPluginSettings(plugin: Plugin): List<PluginSetting> =
        withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                performOperationsWithPlugin(plugin.pluginService) { pluginAdapter ->
                    continuation.resume(pluginAdapter.getPluginSettings())
                }
            }
        }

    override suspend fun setPluginSetting(plugin: Plugin, settingUuid: UUID, newValue: String) =
        withContext(Dispatchers.IO) {
            performOperationsWithPlugin(plugin.pluginService) { pluginAdapter ->
                pluginAdapter.setSetting(settingUuid.toParcelUuid(), newValue)
            }
        }

    private inner class PackageBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) = Unit
    }

    private suspend fun getPluginCommandsIpc(pluginId: String, pluginService: PluginService): List<PluginCommand> =
        withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                performOperationsWithPlugin(pluginService) { adapter ->
                    val commands = adapter.getAllCommands()
                        .map { it.toPluginCommand(pluginId) }

                    continuation.resume(commands)
                }
            }
        }

    private suspend fun getPluginMetadataIpc(pluginService: PluginService): PluginMetadata =
        withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                performOperationsWithPlugin(pluginService) { adapter ->
                    continuation.resume(adapter.getPluginMetadata())
                }
            }
        }

    private suspend fun getPluginSummary(pluginService: PluginService): Result<PluginSummary> =
        withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                performOperationsWithPlugin(pluginService) { adapter ->
                    try {
                        continuation.resume(Result.success(adapter.getPluginSummary()))
                    } catch (ex: RemoteException) {
                        continuation.resume(Result.failure(ex))
                    }
                }
            }
        }

    private fun performOperationsWithPlugin(
        pluginService: PluginService,
        onResult: (PluginInvocationResult) -> Unit = {},
        operationsWithPlugin: (PluginAdapter) -> Unit,
    ) {
        val intent = getIntentForPlugin(pluginService)

        val serviceConnection = getServiceConnectionForPlugin(
            operationsWithPlugin = {
                try {
                    operationsWithPlugin(it)

                    onResult(PluginInvocationResult.Success)
                } catch (ex: DeadObjectException) {
                    onResult(PluginInvocationResult.Failure)
                } catch (ex: RemoteException) {
                    onResult(PluginInvocationResult.Failure)
                }
            }
        )

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

    private fun getServiceConnectionForPlugin(operationsWithPlugin: (PluginAdapter) -> Unit) =
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                val plugin = PluginAdapter.Stub.asInterface(service)

                operationsWithPlugin(plugin)

                context.unbindService(this)
            }

            override fun onServiceDisconnected(name: ComponentName?) = Unit
        }
}