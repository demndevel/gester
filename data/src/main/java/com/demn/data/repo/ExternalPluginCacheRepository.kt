package com.demn.data.repo

import com.demn.data.dao.PluginCacheDao
import com.demn.data.entities.PluginWithCommandsDbo
import com.demn.data.toPluginCache
import com.demn.data.toPluginWithCommandsDbo
import com.demn.domain.data.PluginCache
import com.demn.domain.data.ExternalPluginCacheRepository
import java.util.*

class MockExternalPluginCacheRepository() : ExternalPluginCacheRepository {
    override suspend fun getAllPlugins(): List<PluginCache> = emptyList()

    override suspend fun getPluginCache(uuid: UUID): PluginCache? = null

    override suspend fun updatePluginCache(pluginCache: PluginCache) = Unit

    override suspend fun removePluginCache(uuid: UUID) = Unit
}

class ExternalPluginCacheRepositoryImpl(
    private val pluginCacheDao: PluginCacheDao
) : ExternalPluginCacheRepository {
    override suspend fun getAllPlugins(): List<PluginCache> {
        return pluginCacheDao
            .getPluginsWithCommands()
            .map(PluginWithCommandsDbo::toPluginCache)
    }

    override suspend fun getPluginCache(uuid: UUID): PluginCache? {
        return pluginCacheDao
            .getPluginWithCommands(uuid)
            ?.toPluginCache()
    }

    override suspend fun updatePluginCache(pluginCache: PluginCache) {
        pluginCacheDao.insertPluginWithCommands(pluginCache.toPluginWithCommandsDbo())
    }

    override suspend fun removePluginCache(uuid: UUID) {
        pluginCacheDao.deletePluginWithCorrespondingData(uuid)
    }
}
