package com.demn.data

import com.demn.data.entities.ResultFrecencyDbo
import com.demn.domain.models.ResultFrecency

fun ResultFrecencyDbo.toResultFrecency(): ResultFrecency {
    return ResultFrecency(
        resultHashCode = resultHashCode,
        input = input,
        usages = usages,
        recency = recency
    )
}