package com.demn.domain.usecase

import com.demn.domain.data.ResultFrecencyRepository
import com.demn.domain.models.ResultFrecency
import com.demn.plugincore.operationresult.BasicOperationResult
import com.demn.plugincore.operationresult.CommandOperationResult
import com.demn.plugincore.operationresult.IconOperationResult
import com.demn.plugincore.operationresult.OperationResult
import com.demn.plugincore.operationresult.ResultType
import com.frosch2010.fuzzywuzzy_kotlin.FuzzySearch
import com.frosch2010.fuzzywuzzy_kotlin.ToStringFunction
import com.michaeltroger.latintocyrillic.Alphabet
import com.michaeltroger.latintocyrillic.LatinCyrillicFactory
import kotlinx.coroutines.runBlocking

interface OperationResultSorterUseCase {
    suspend operator fun invoke(
        input: String,
        results: List<OperationResult>
    ): List<OperationResult>
}

class MockOperationResultSorterUseCase : OperationResultSorterUseCase {
    override suspend operator fun invoke(
        input: String, results: List<OperationResult>
    ): List<OperationResult> {
        return results
    }
}

class OperationResultFuzzySearcherToString : ToStringFunction<OperationResult> {
    override fun apply(item: OperationResult): String = runBlocking {
        if (item is CommandOperationResult) return@runBlocking latinise(item.name)
        if (item is BasicOperationResult) return@runBlocking latinise(item.text)
        if (item is IconOperationResult) return@runBlocking latinise(item.text)
        return@runBlocking ""
    }
}

private suspend fun latinise(input: String): String {
    val latinCyrillic = LatinCyrillicFactory.create(Alphabet.RussianIso9)

    return if (latinCyrillic.isCyrillic(input)) latinCyrillic.cyrillicToLatin(input) else input
}

class OperationResultSorterUseCaseImpl(
    private val resultFrecencyRepository: ResultFrecencyRepository
) : OperationResultSorterUseCase {
    override suspend operator fun invoke(
        input: String,
        results: List<OperationResult>
    ): List<OperationResult> {
        val fuzzySorted = fuzzySort(input, results)

        return sortByFrecency(input, fuzzySorted)
    }

    private suspend fun fuzzySort(
        input: String,
        results: List<OperationResult>
    ): List<OperationResult> {
        val fuzzyExtracted = FuzzySearch.extractAll(
            query = latinise(input),
            choices = results,
            toStringFunction = OperationResultFuzzySearcherToString()
        )

        val fuzzySorted = fuzzyExtracted
            .sortedByDescending { it.score }
            .map { it.referent }

        return fuzzySorted
    }

    private suspend fun sortByFrecency(
        input: String,
        fuzzySorted: List<OperationResult>
    ): List<OperationResult> {
        val frecencies = resultFrecencyRepository.getUsagesByInput(input)

        val frecencyMap = frecencies.associateBy { it.resultHashCode }

        val combinedResults = fuzzySorted.map { result ->
            val frecency = frecencyMap[result.hashCode()]
            result to frecency
        }

        val sortedResults = combinedResults
            .sortedWith(
                compareByDescending<Pair<OperationResult, ResultFrecency?>> { it.second?.usages }
                    .thenByDescending { it.second?.recency }
                    .thenByDescending { it.first.pinToTop }
            )


        return sortedResults.map { it.first }
    }
}