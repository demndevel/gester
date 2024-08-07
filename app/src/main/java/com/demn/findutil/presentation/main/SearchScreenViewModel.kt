package com.demn.findutil.presentation.main

import android.content.Intent
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demn.domain.data.ResultFrecencyRepository
import com.demn.domain.plugin_management.PluginRepository
import com.demn.domain.usecase.ProcessInputQueryUseCase
import com.demn.plugincore.Plugin
import com.demn.plugincore.PluginFallbackCommand
import com.demn.plugincore.operation_result.BasicOperationResult
import com.demn.plugincore.operation_result.CommandOperationResult
import com.demn.plugincore.operation_result.IconOperationResult
import com.demn.plugincore.operation_result.OperationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

@Immutable
data class SearchScreenState(
    val searchResults: List<OperationResult> = emptyList(),
    val pluginList: List<Plugin> = emptyList(),
    val fallbackCommands: List<PluginFallbackCommand> = emptyList()
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

    fun loadPlugins() {
        viewModelScope.launch(Dispatchers.IO) {
            val plugins = pluginRepository.getPluginList()
            val fallbackCommands = pluginRepository.getAllFallbackCommands()
            _state.update {
                it.copy(
                    pluginList = plugins,
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
    }

    fun updateSearchBarValue(newValue: String, onError: () -> Unit = {}) {
        if (newValue.isBlank()) {
            searchBarState = newValue
            _state.update {
                it.copy(searchResults = emptyList())
            }
            return
        }

        searchBarState = newValue

        viewModelScope.launch(Dispatchers.IO) {
            val results = processQueryUseCase(
                plugins = _state.value.pluginList,
                inputQuery = newValue,
                onError = {
                    onError()
                }
            )

            _state.update {
                it.copy(searchResults = results)
            }
        }
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