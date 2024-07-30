package com.demn.domain.usecase

import com.demn.domain.data.PluginCache
import com.demn.domain.data.PluginCommandCacheRepository
import com.demn.domain.plugin_providers.ExternalPluginsProvider

interface PluginCacheSyncUseCase {
    suspend operator fun invoke()
}

class MockPluginCacheSyncUseCase : PluginCacheSyncUseCase {
    override suspend fun invoke() = Unit
}

class PluginCacheSyncUseCaseImpl(
    private val pluginCommandCacheRepository: PluginCommandCacheRepository,
    private val externalPluginsProvider: ExternalPluginsProvider
) : PluginCacheSyncUseCase {
    override suspend operator fun invoke() {
        externalPluginsProvider.getPluginList().forEach {
            val commands = externalPluginsProvider.getPluginCommandsDirectly(it)
            pluginCommandCacheRepository.updatePluginCache(
                pluginCache = PluginCache(
                    pluginUuid = it.metadata.pluginUuid,
                    name = it.metadata.pluginName,
                    description = it.metadata.description,
                    version = it.metadata.version,
                    consumeAnyInput = it.metadata.consumeAnyInput,
                    commands = commands
                )
            )
        }
    }
}