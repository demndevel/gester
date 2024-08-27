package com.demn.findutil.presentation.main

import android.content.Intent
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demn.domain.data.ResultFrecencyRepository
import com.demn.domain.models.PluginFallbackCommand
import com.demn.domain.pluginmanagement.PluginRepository
import com.demn.domain.usecase.ProcessInputQueryUseCase
import com.demn.domain.models.Plugin
import com.demn.domain.models.PluginError
import com.demn.plugincore.operationresult.BasicOperationResult
import com.demn.plugincore.operationresult.CommandOperationResult
import com.demn.plugincore.operationresult.IconOperationResult
import com.demn.plugincore.operationresult.OperationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
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
    private val _state = MutableStateFlow(SearchScreenState())

    val state = _state.asStateFlow()

    var searchBarState by mutableStateOf("")
        private set

    private val _searchQueryState = MutableStateFlow("")

    init {
        viewModelScope.launch {
            _searchQueryState
                .collectLatest { query ->
                    delay(50)

                    if (query.isBlank()) return@collectLatest

                    val results = processQueryUseCase(
                        plugins = _state.value.pluginList,
                        inputQuery = query
                    )

                    _state.update {
                        it.copy(searchResults = results)
                    }
                }
        }
    }

    fun loadPlugins() {
        viewModelScope.launch(Dispatchers.IO) {
            val getPluginsResult = pluginRepository.getPluginList()
            val fallbackCommands = pluginRepository.getAllFallbackCommands()
            _state.update {
                it.copy(
                    pluginList = getPluginsResult.plugins,
                    pluginErrors = getPluginsResult.pluginErrors,
                    fallbackCommands = fallbackCommands
                )
            }
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
            _state.update {
                it.copy(searchResults = emptyList())
            }
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
                pluginRepository.invokeCommand(operationResult.uuid, operationResult.pluginUuid)
            }

            if (operationResult is CommandOperationResult || operationResult is BasicOperationResult || operationResult is IconOperationResult) {
                updateSearchBarValue("")
            }
        }
    }
}