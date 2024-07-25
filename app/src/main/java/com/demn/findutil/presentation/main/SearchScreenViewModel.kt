package com.demn.findutil.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demn.findutil.usecase.ProcessQueryUseCase
import com.demn.plugincore.Plugin
import com.demn.plugincore.PluginFallbackCommand
import com.demn.plugincore.operation_result.OperationResult
import com.demn.pluginloading.PluginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

data class SearchScreenState(
    val searchBarValue: String = "",
    val searchResults: List<OperationResult> = emptyList(),
    val pluginList: List<Plugin> = emptyList(),
    val fallbackCommands: List<PluginFallbackCommand> = emptyList()
)

class SearchScreenViewModel(
    private val pluginRepository: PluginRepository,
    private val processQueryUseCase: ProcessQueryUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(SearchScreenState())

    val state = _state.asStateFlow()

    fun loadPlugins() {
        viewModelScope.launch(Dispatchers.IO) {
            val plugins = pluginRepository.getPluginList()
            val fallbackCommands = pluginRepository.getAllFallbackCommands()

            println("fallbackCommands:" + fallbackCommands)

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
            it.copy(
                searchBarValue = newValue
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            val results = processQueryUseCase.invoke(_state.value.pluginList, newValue)

            _state.update {
                it.copy(
                    searchResults = results
                )
            }
        }
    }
}