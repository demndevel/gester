package com.demn.gester.presentation.main.components

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
import com.demn.gester.R
import com.demn.gester.presentation.main.SearchScreenState
import io.github.demndevel.gester.core.operationresult.OperationResult
import java.util.*

@Composable
fun SearchScreenContent(
    state: SearchScreenState,
    focusRequester: FocusRequester,
    onResultClick: (OperationResult) -> Unit,
    searchBarState: String,
    onFallbackCommandClick: (id: UUID) -> Unit,
    onSearchBarValueChange: (String) -> Unit,
    onEnterClick: () -> Unit,
    onResultLongClick: (OperationResult) -> Unit,
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
            if (searchBarState.isBlank() && state.pluginErrors.isNotEmpty()) {
                item {
                    PluginErrorList(state.pluginErrors)
                }
            }

            resultsList(
                state,
                onResultClick = onResultClick,
                onResultLongClick = onResultLongClick
            )

            item { Spacer(Modifier.height(4.dp)) }

            if (searchBarState.isNotBlank() && state.fallbackCommands.isNotEmpty()) {
                item {
                    FallbackCommandsResultsList(
                        currentInput = searchBarState,
                        fallbackCommands = state.fallbackCommands,
                        onFallbackCommandClick = onFallbackCommandClick
                    )
                }
            }
        }

        SearchBar(
            focusRequester = focusRequester,
            searchBarValue = searchBarState,
            onSearchBarValueChange = onSearchBarValueChange,
            onEnterClick = onEnterClick,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

private fun LazyListScope.resultsList(
    state: SearchScreenState,
    onResultClick: (OperationResult) -> Unit,
    onResultLongClick: (OperationResult) -> Unit,
) {
    itemsIndexed(state.searchResults) { index, item ->
        ResultItem(
            item,
            index,
            onResultClick = onResultClick,
            onResultLongClick = onResultLongClick,
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
