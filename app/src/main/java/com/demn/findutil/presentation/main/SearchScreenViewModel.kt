package com.demn.findutil.presentation.main

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demn.domain.data.ResultFrecencyRepository
import com.demn.domain.plugin_management.PluginRepository
import com.demn.domain.usecase.ProcessInputQueryUseCase
import com.demn.plugincore.Plugin
import com.demn.plugincore.PluginFallbackCommand
import com.demn.plugincore.operation_result.BasicOperationResult
import com.demn.plugincore.operation_result.CommandOperationResult
import com.demn.plugincore.operation_result.OperationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class SearchScreenState(
    val searchBarValue: String = "",
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
                input = state.value.searchBarValue,
                commandUuid = id
            )
        }
    }

    fun updateSearchBarValue(newValue: String) {
        if (newValue.isEmpty()) {
            _state.update {
                it.copy(
                    searchBarValue = newValue,
                    searchResults = emptyList()
                )
            }

            return
        }

        _state.update {
            it.copy(searchBarValue = newValue)
        }

        viewModelScope.launch {
            val results = processQueryUseCase(_state.value.pluginList, newValue)

            _state.update {
                it.copy(searchResults = results)
            }
        }
    }

    fun executeResult(operationResult: OperationResult, runIntent: (Intent) -> Unit) {
        viewModelScope.launch {
            resultFrecencyRepository.incrementUsages(
                state.value.searchBarValue,
                hashCode = operationResult.hashCode(),
                recencyTimestamp = System.currentTimeMillis()
            )

            if (operationResult is BasicOperationResult) {
                operationResult.intent?.let { runIntent(it) }
            }

            if (operationResult is CommandOperationResult) {
                pluginRepository.invokeCommand(operationResult.uuid, operationResult.pluginUuid)
            }

            if (operationResult is CommandOperationResult || operationResult is BasicOperationResult) {
                _state.update {
                    it.copy(searchBarValue = "")
                }
            }
        }
    }
}