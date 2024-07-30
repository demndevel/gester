package com.demn.data.repo

import com.demn.data.dao.PluginCacheDao
import com.demn.data.entities.PluginWithCommandsDbo
import com.demn.data.toPluginCache
import com.demn.data.toPluginCommand
import com.demn.data.toPluginWithCommandsDbo
import com.demn.domain.data.PluginCache
import com.demn.domain.data.PluginCommandCacheRepository

class MockPluginCommandCacheRepository() : PluginCommandCacheRepository {
    override suspend fun getAllPlugins(): List<PluginCache> = emptyList()

    override suspend fun updatePluginCache(pluginCache: PluginCache) = Unit
}

class PluginCommandCacheRepositoryImpl(
    private val pluginCacheDao: PluginCacheDao
) : PluginCommandCacheRepository {
    override suspend fun getAllPlugins(): List<PluginCache> {
        return pluginCacheDao
            .getPluginsWithCommands()
            .map(PluginWithCommandsDbo::toPluginCache)
    }

    override suspend fun updatePluginCache(pluginCache: PluginCache) {
        pluginCacheDao.insertPluginWithCommands(pluginCache.toPluginWithCommandsDbo())
    }
}
