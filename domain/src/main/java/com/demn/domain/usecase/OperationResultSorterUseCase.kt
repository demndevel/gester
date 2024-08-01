package com.demn.domain.usecase

import com.demn.domain.data.ResultFrecencyRepository
import com.demn.plugincore.operation_result.BasicOperationResult
import com.demn.plugincore.operation_result.CommandOperationResult
import com.demn.plugincore.operation_result.OperationResult
import com.frosch2010.fuzzywuzzy_kotlin.FuzzySearch
import com.frosch2010.fuzzywuzzy_kotlin.ToStringFunction

interface OperationResultSorterUseCase {
    operator fun invoke(input: String, results: List<OperationResult>): List<OperationResult>
}

class MockOperationResultSorterUseCase : OperationResultSorterUseCase {
    override operator fun invoke(
        input: String, results: List<OperationResult>
    ): List<OperationResult> {
        return results
    }
}

class OperationResultFuzzySearcherToString : ToStringFunction<OperationResult> {
    override fun apply(item: OperationResult): String {
        if (item is CommandOperationResult) return item.name
        if (item is BasicOperationResult) return item.text
        return ""
    }
}

class OperationResultSorterUseCaseImpl(
    private val resultFrecencyRepository: ResultFrecencyRepository
) : OperationResultSorterUseCase {
    override operator fun invoke(
        input: String, results: List<OperationResult>
    ): List<OperationResult> {
        // TODO

        val fuzzyExtracted = FuzzySearch.extractAll(
            query = input,
            choices = results,
            toStringFunction = OperationResultFuzzySearcherToString()
        )

        val fuzzySorted = fuzzyExtracted.sortedByDescending { it.score }

        return fuzzySorted.map { println(it); it.referent }
    }
}