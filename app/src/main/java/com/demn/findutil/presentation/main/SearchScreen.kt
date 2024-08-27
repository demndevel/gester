package com.demn.findutil.presentation.main

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.demn.domain.models.PluginFallbackCommand
import com.demn.findutil.presentation.main.components.SearchScreenContent
import com.demn.plugincore.operationresult.CommandOperationResult
import org.koin.androidx.compose.koinViewModel
import java.util.*

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    vm: SearchScreenViewModel = koinViewModel<SearchScreenViewModel>(),
) {
    val context = LocalContext.current
    val state by vm.state.collectAsState()
    val searchBarFocusRequester = remember { FocusRequester() }
    val firstResult = state.searchResults.firstOrNull()

    LaunchedEffect(Unit) {
        vm.loadPlugins()
    }

    SearchScreenContent(
        state = state,
        focusRequester = searchBarFocusRequester,
        onFallbackCommandClick = vm::invokeFallbackCommand,
        onSearchBarValueChange = {
            vm.updateSearchBarValue(newValue = it)
        },
        onEnterClick = {
            firstResult?.let {
                vm.executeResult(firstResult, context::startActivity)
            }
        },
        onResultClick = {
            vm.executeResult(it, context::startActivity)
        },
        searchBarState = vm.searchBarState,
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
    )
}

@Preview
@Composable
private fun SearchScreenPreview() {
    Box(Modifier.fillMaxSize()) {
        SearchScreenContent(
            modifier = Modifier.fillMaxSize(),
            state =  SearchScreenState(
                searchResults = listOf(
                    CommandOperationResult(
                        uuid = UUID.randomUUID(),
                        pluginUuid = UUID.randomUUID(),
                        iconUri = Uri.EMPTY,
                        name = "Test command"
                    )
                ),
                fallbackCommands = listOf(
                    PluginFallbackCommand(
                        uuid = UUID.randomUUID(),
                        pluginUuid = UUID.randomUUID(),
                        name = "Test fallback command",
                        iconUri = Uri.EMPTY,
                    )
                ),
            ),
            focusRequester = remember { FocusRequester() },
            onResultClick = {},
            searchBarState = "Test",
            onFallbackCommandClick = {},
            onSearchBarValueChange = {},
            onEnterClick = {},
        )
    }
}