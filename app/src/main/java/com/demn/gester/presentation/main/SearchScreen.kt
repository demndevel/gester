package com.demn.gester.presentation.main

import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import com.demn.domain.models.PluginFallbackCommand
import com.demn.gester.R
import com.demn.gester.presentation.main.components.SearchScreenContent
import com.demn.gester.presentation.main.components.getOperationResultClipboardText
import io.github.demndevel.gester.core.operationresult.CommandOperationResult
import org.koin.androidx.compose.koinViewModel
import java.util.*

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    vm: SearchScreenViewModel = koinViewModel<SearchScreenViewModel>(),
) {
    val context = LocalContext.current
    val state by vm.state
    val searchBarFocusRequester = remember { FocusRequester() }
    val firstResult = state.searchResults.firstOrNull()
    val clipboardManager = LocalClipboardManager.current
    val copiedText = stringResource(R.string.copied)

    LaunchedEffect(Unit) {
        vm.loadPlugins()
    }

    SearchScreenContent(
        state = state,
        focusRequester = searchBarFocusRequester,
        onResultClick = {
            vm.executeResult(it, context::startActivity)
        },
        searchBarState = vm.searchBarState,
        onFallbackCommandClick = vm::invokeFallbackCommand,
        onSearchBarValueChange = {
            vm.updateSearchBarValue(newValue = it)
        },
        onEnterClick = {
            firstResult?.let {
                vm.executeResult(firstResult, context::startActivity)
            }
        },
        onResultLongClick = { result ->
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                Toast.makeText(context, copiedText, Toast.LENGTH_SHORT).show()
            }

            clipboardManager.setText(AnnotatedString(getOperationResultClipboardText(result)))
        },
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
            state =  SearchScreenState(
                searchResults = listOf(
                    CommandOperationResult(
                        uuid = UUID.randomUUID(),
                        pluginId = "someid",
                        iconUri = Uri.EMPTY,
                        name = "Test command"
                    )
                ),
                fallbackCommands = listOf(
                    PluginFallbackCommand(
                        uuid = UUID.randomUUID(),
                        pluginId = "someid",
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
            onResultLongClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
