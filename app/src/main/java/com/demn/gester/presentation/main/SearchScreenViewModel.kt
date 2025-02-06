package com.demn.gester.presentation.main

import android.content.Intent
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demn.domain.data.ResultFrecencyRepository
import com.demn.domain.models.Plugin
import com.demn.domain.models.PluginError
import com.demn.domain.models.PluginFallbackCommand
import com.demn.domain.pluginmanagement.PluginRepository
import com.demn.domain.usecase.ProcessInputQueryUseCase
import io.github.demndevel.gester.core.operationresult.BasicOperationResult
import io.github.demndevel.gester.core.operationresult.CommandOperationResult
import io.github.demndevel.gester.core.operationresult.IconOperationResult
import io.github.demndevel.gester.core.operationresult.OperationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID

@Immutable
data class SearchScreenState(
    val searchResults: List<OperationResult> = emptyList(),
    val pluginList: List<Plugin> = emptyList(),
    val fallbackCommands: List<PluginFallbackCommand> = emptyList(),
    val pluginErrors: List<PluginError> = emptyList()
)

class SearchScreenViewModel(
    private val pluginRepository: PluginRepository,
    private val processQueryUseCase: ProcessInputQueryUseCase,
    private val resultFrecencyRepository: ResultFrecencyRepository
) : ViewModel() {
    var state = mutableStateOf(SearchScreenState())
        private set

    var searchBarState by mutableStateOf("")
        private set

    private val _searchQueryState = MutableStateFlow("")

    init {
        viewModelScope.launch {
            _searchQueryState
                .collectLatest { query ->
                    if (query.isBlank()) return@collectLatest

                    val resultsFlow = processQueryUseCase(
                        plugins = state.value.pluginList,
                        inputQuery = query
                    )

                    resultsFlow.collectLatest { newResults ->
                        state.value = state.value.copy(
                            searchResults = newResults
                        )
                    }
                }
        }
    }

    fun loadPlugins() {
        viewModelScope.launch {
            val getPluginsResult = pluginRepository.getPluginList()
            val fallbackCommands = pluginRepository.getAllFallbackCommands()
            state.value = state.value.copy(
                pluginList = getPluginsResult.plugins,
                pluginErrors = getPluginsResult.pluginErrors,
                fallbackCommands = fallbackCommands
            )
        }
    }

    fun invokeFallbackCommand(id: UUID) {
        viewModelScope.launch {
            pluginRepository.invokeFallbackCommand(
                input = searchBarState,
                commandUuid = id
            )
        }

        updateSearchBarValue("")
    }

    fun updateSearchBarValue(newValue: String) {
        if (newValue.isBlank()) {
            state.value = state.value.copy(searchResults = emptyList())
        }

        searchBarState = newValue

        _searchQueryState.tryEmit(newValue)
    }

    fun executeResult(operationResult: OperationResult, runIntent: (Intent) -> Unit) {
        viewModelScope.launch {
            resultFrecencyRepository.incrementUsages(
                searchBarState,
                hashCode = operationResult.hashCode(),
                recencyTimestamp = System.currentTimeMillis()
            )

            if (operationResult is BasicOperationResult) {
                operationResult.intent?.let { runIntent(it) }
            }

            if (operationResult is IconOperationResult) {
                operationResult.intent?.let { runIntent(it) }
            }

            if (operationResult is CommandOperationResult) {
                pluginRepository.invokeCommand(operationResult.uuid, operationResult.pluginId)
            }

            if (operationResult is CommandOperationResult || operationResult is BasicOperationResult || operationResult is IconOperationResult) {
                updateSearchBarValue("")
            }
        }
    }
}
