package com.demn.domain.usecase

import com.demn.domain.data.PluginCache
import com.demn.domain.data.ExternalPluginCacheRepository
import com.demn.domain.plugin_providers.ExternalPluginsProvider

interface PluginCacheSyncUseCase {
    suspend operator fun invoke()
}

class MockPluginCacheSyncUseCase : PluginCacheSyncUseCase {
    override suspend fun invoke() = Unit
}

class PluginCacheSyncUseCaseImpl(
    private val externalPluginsProvider: ExternalPluginsProvider
) : PluginCacheSyncUseCase {
    override suspend operator fun invoke() {
        externalPluginsProvider.getPluginList()
    }
}