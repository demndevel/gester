package com.demn.findutil.presentation.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.demn.data.repo.MockResultFrecencyRepository
import com.demn.domain.usecase.MockCommandSearcherUseCase
import com.demn.domain.usecase.MockOperationResultSorterUseCase
import com.demn.domain.usecase.ProcessInputQueryUseCase
import com.demn.findutil.app_settings.MockPluginAvailabilityRepository
import com.demn.findutil.presentation.main.components.SearchScreenContent
import com.demn.pluginloading.MockPluginRepository
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    vm: SearchScreenViewModel = koinViewModel<SearchScreenViewModel>(),
) {
    val context = LocalContext.current
    val state by vm.state.collectAsState()
    val searchBarFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        vm.loadPlugins()
    }

    SearchScreenContent(
        vm,
        state,
        context,
        searchBarFocusRequester,
        modifier
            .fillMaxSize()
            .imePadding(),
    )
}

@Preview
@Composable
private fun SearchScreenPreview() {
    Surface(Modifier.fillMaxSize()) {
        SearchScreen(
            modifier = Modifier.fillMaxSize(),
            vm = SearchScreenViewModel(
                MockPluginRepository(),
                ProcessInputQueryUseCase(
                    MockPluginRepository(),
                    MockOperationResultSorterUseCase(),
                    MockPluginAvailabilityRepository(),
                    MockCommandSearcherUseCase()
                ),
                MockResultFrecencyRepository()
            ),
        )
    }
}