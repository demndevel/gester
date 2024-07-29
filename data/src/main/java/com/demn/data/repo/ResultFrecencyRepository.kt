package com.demn.data.repo

import com.demn.data.dao.ResultFrecencyDao
import com.demn.data.entities.ResultFrecencyDbo
import com.demn.data.toResultFrecency
import com.demn.domain.data.ResultFrecencyRepository
import com.demn.domain.models.ResultFrecency

class MockResultFrecencyRepository : ResultFrecencyRepository {
    override suspend fun incrementUsages(input: String, hashCode: Int, recencyTimestamp: Long) = Unit

    override suspend fun getUsagesByInput(input: String): List<ResultFrecency> = emptyList()
}

class ResultFrecencyRepositoryImpl(
    private val frecencyDao: ResultFrecencyDao
) : ResultFrecencyRepository {
    override suspend fun incrementUsages(input: String, hashCode: Int, recencyTimestamp: Long) {
        frecencyDao.getResultFrecency(input, hashCode).let {
            if (it == null) {
                frecencyDao.insertResultFrecency(
                    ResultFrecencyDbo(
                        input = input,
                        resultHashCode = hashCode,
                        usages = 1,
                        recency = recencyTimestamp
                    )
                )

                return
            }

            frecencyDao.updateResultFrecency(
                it.copy(
                    usages = it.usages.plus(1),
                    recency = recencyTimestamp,
                )
            )
        }
    }

    override suspend fun getUsagesByInput(input: String): List<ResultFrecency> {
        return frecencyDao
            .getUsagesByInput(input)
            .map(ResultFrecencyDbo::toResultFrecency)
    }
}