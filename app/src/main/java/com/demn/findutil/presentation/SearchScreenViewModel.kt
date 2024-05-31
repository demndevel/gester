package com.demn.findutil.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demn.findutil.plugins.PluginRepository
import com.demn.findutil.usecase.ProcessQueryUseCase
import com.demn.plugincore.Plugin
import com.demn.plugincore.operation_result.OperationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchScreenState(
    val searchBarValue: String = "",
    val searchResults: List<OperationResult> = listOf(),
    val pluginList: List<Plugin> = listOf()
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

            _state.update {
                it.copy(
                    pluginList = plugins
                )
            }
        }
    }

    fun updateSearchBarValue(newValue: String) {
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