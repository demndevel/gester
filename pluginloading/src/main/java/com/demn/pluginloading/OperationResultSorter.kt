package com.demn.pluginloading

import com.demn.domain.plugin_management.OperationResultSorter
import com.demn.domain.data.ResultFrecencyRepository
import com.demn.plugincore.operation_result.OperationResult

class MockOperationResultSorter : OperationResultSorter {
    override fun sort(results: List<OperationResult>): List<OperationResult> {
        return results
    }
}

class OperationResultSorterImpl(
    private val resultFrecencyRepository: ResultFrecencyRepository
) : OperationResultSorter {
    override fun sort(results: List<OperationResult>): List<OperationResult> {
        // TODO
        return results
    }
}