package com.demn.domain.usecase

import com.demn.domain.data.ExternalPluginCacheRepository
import com.demn.domain.data.PluginCache
import com.demn.domain.models.ExternalPlugin
import com.demn.domain.pluginproviders.ExternalPluginsProvider

interface PluginCacheSyncUseCase {
    suspend operator fun invoke()
}

class MockPluginCacheSyncUseCase : PluginCacheSyncUseCase {
    override suspend fun invoke() = Unit
}

class PluginCacheSyncUseCaseImpl(
    private val externalPluginsProvider: ExternalPluginsProvider,
    private val externalPluginCacheRepository: ExternalPluginCacheRepository
) : PluginCacheSyncUseCase {
    override suspend operator fun invoke() {
        val plugins = externalPluginsProvider.getPluginList()
        val pluginsCache = externalPluginCacheRepository.getAllPlugins()
        removeUnusedPluginData(plugins, pluginsCache)
    }

    private suspend fun removeUnusedPluginData(plugins: List<ExternalPlugin>, pluginsCache: List<PluginCache>) {
        pluginsCache.forEach { pluginCache ->
            if (!plugins.any { it.metadata.pluginUuid == pluginCache.pluginMetadata.pluginUuid }) {
                externalPluginCacheRepository.removePluginCache(pluginCache.pluginMetadata.pluginUuid)
            }
        }
    }
}