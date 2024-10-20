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
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

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
            .map(::calculateFrecencyPoints)
            .sortedWith(
                compareByDescending<Pair<OperationResult, Int>> { it.second }
                    .thenByDescending { it.first.pinToTop }
            )


        return sortedResults.map { it.first }
    }

    private fun calculateFrecencyPoints(
        pair: Pair<OperationResult, ResultFrecency?>
    ): Pair<OperationResult, Int> {
        pair.second?.let { frecency ->
            val recency = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(frecency.recency),
                ZoneId.systemDefault()
            )
            val nowLocalDateTime = LocalDateTime.now()

            val recencyPoints = when (recency) {
                in nowLocalDateTime.minusDays(4)..nowLocalDateTime -> 100

                in nowLocalDateTime.minusDays(14)..nowLocalDateTime.minusDays(4) -> 70

                in nowLocalDateTime.minusDays(31)..nowLocalDateTime.minusDays(14) -> 50

                in nowLocalDateTime.minusDays(90)..nowLocalDateTime.minusDays(31) -> 30

                else -> 20
            }

            val frequencyPoints = frecency.usages * 20

            val totalPoints = recencyPoints + frequencyPoints

            return Pair(pair.first, totalPoints)
        }

        return Pair(pair.first, 0)
    }
}