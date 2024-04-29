package com.demn.findutil

import androidx.lifecycle.ViewModel
import com.demn.findutil.models.Application
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SearchScreenState(
    val searchBarValue: String = "",
    val foundApplications: List<Application> = listOf()
)

class SearchScreenViewModel(
    private val applicationSearcher: ApplicationSearcher
) : ViewModel() {
    private val _state = MutableStateFlow(SearchScreenState())

    val state = _state.asStateFlow()

    fun updateSearchBarValue(newValue: String) {
        val foundApps = applicationSearcher.find(newValue)

        _state.update {
            it.copy(
                searchBarValue = newValue,
                foundApplications = foundApps
            )
        }
    }
}