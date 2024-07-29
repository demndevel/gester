package com.demn.domain.data

import com.demn.domain.models.ResultFrecency

interface ResultFrecencyRepository {
    suspend fun incrementUsages(input: String, hashCode: Int, recencyTimestamp: Long)

    suspend fun getUsagesByInput(input: String): List<ResultFrecency>
}