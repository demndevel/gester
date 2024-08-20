package com.demn.findutil.presentation.main.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.demn.domain.models.PluginFallbackCommand
import com.demn.findutil.R
import com.demn.findutil.presentation.main.SearchScreenState
import com.demn.findutil.presentation.main.SearchScreenViewModel
import java.util.UUID

@Composable
fun SearchScreenContent(
    vm: SearchScreenViewModel,
    state: SearchScreenState,
    context: Context,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            resultsList(state, vm, context)

            item { Spacer(Modifier.height(4.dp)) }

            if (vm.searchBarState.isNotBlank()) {
                item {
                    FallbackCommandsResultsList(
                        currentInput = vm.searchBarState,
                        fallbackCommands = state.fallbackCommands,
                        onFallbackCommandClick = vm::invokeFallbackCommand
                    )
                }
            }
        }

        SearchBar(
            focusRequester = focusRequester,
            searchBarValue = vm.searchBarState,
            onSearchBarValueChange = {
                vm.updateSearchBarValue(
                    it,
                    onError = {
                        Toast(context)
                            .apply {
                                setText("some error with plugin N occured") // TODO
                            }
                            .show()
                    }
                )
            },
            onEnterClick = {
                val firstResult = state.searchResults.firstOrNull()

                firstResult?.let {
                    vm.executeResult(firstResult, context::startActivity)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

private fun LazyListScope.resultsList(
    state: SearchScreenState,
    vm: SearchScreenViewModel,
    context: Context
) {
    itemsIndexed(state.searchResults) { index, item ->
        ResultItem(
            item,
            index,
            onResultClick = { vm.executeResult(it, context::startActivity) },
            Modifier
        )
    }
}

@Composable
private fun FallbackCommandsResultsList(
    currentInput: String,
    fallbackCommands: List<PluginFallbackCommand>,
    onFallbackCommandClick: (id: UUID) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(Modifier) {
            Text(
                text = stringResource(R.string.use_input_with_fallback_command, currentInput),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(8.dp)
            )
        }

        fallbackCommands.forEach { command ->
            FallbackCommandsResult(
                fallbackCommand = command,
                onFallbackCommandClick = onFallbackCommandClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}