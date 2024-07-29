package com.demn.domain.models

/**
 * @param[resultHashCode] result hash code used to identify result
 * @param[input] search query
 * @param[usages] usages count
 * @param[recency] unix timestamp in milliseconds of the most recent usage of the command
 */
data class ResultFrecency(
    val resultHashCode: Int,
    val input: String,
    val usages: Int,
    val recency: Long
)