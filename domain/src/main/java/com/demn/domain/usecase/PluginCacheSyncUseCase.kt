package com.demn.domain.usecase

import com.demn.domain.data.PluginCacheRepository
import com.demn.domain.data.PluginCache
import com.demn.domain.models.Plugin
import com.demn.domain.pluginproviders.BoundServicePluginsProvider

interface PluginCacheSyncUseCase {
    suspend operator fun invoke()
}

class MockPluginCacheSyncUseCase : PluginCacheSyncUseCase {
    override suspend fun invoke() = Unit
}

class PluginCacheSyncUseCaseImpl(
    private val boundServicePluginsProvider: BoundServicePluginsProvider,
    private val pluginCacheRepository: PluginCacheRepository
) : PluginCacheSyncUseCase {
    override suspend operator fun invoke() {
        val plugins = boundServicePluginsProvider.getPluginList()
        val pluginsCache = pluginCacheRepository.getAllPlugins()
        removeUnusedPluginData(plugins.plugins, pluginsCache)
    }

    private suspend fun removeUnusedPluginData(plugins: List<Plugin>, pluginsCache: List<PluginCache>) {
        pluginsCache.forEach { pluginCache ->
            if (!plugins.any { it.metadata.pluginId == pluginCache.pluginMetadata.pluginId }) {
                pluginCacheRepository.removePluginCache(pluginCache.pluginMetadata.pluginId)
            }
        }
    }
}