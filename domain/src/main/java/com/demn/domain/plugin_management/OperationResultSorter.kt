package com.demn.domain.plugin_management

import com.demn.plugincore.operation_result.OperationResult

interface OperationResultSorter {
    fun sort(results: List<OperationResult>): List<OperationResult>
}