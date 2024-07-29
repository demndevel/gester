package com.demn.data.entities

import androidx.room.Entity

@Entity(primaryKeys = ["resultHashCode", "input"])
data class ResultFrecencyDbo(
    val resultHashCode: Int,
    val input: String,
    val usages: Int,
    val recency: Long
)