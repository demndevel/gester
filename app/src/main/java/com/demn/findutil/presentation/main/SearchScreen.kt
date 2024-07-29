package com.demn.findutil.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.demn.data.repo.MockPluginCacheRepository
import com.demn.data.repo.MockResultFrecencyRepository
import com.demn.domain.usecase.ProcessInputQueryUseCase
import com.demn.findutil.R
import com.demn.findutil.app_settings.MockAppSettingsRepositoryImpl
import com.demn.findutil.app_settings.MockPluginAvailabilityRepository
import com.demn.plugincore.PluginFallbackCommand
import com.demn.plugincore.operation_result.BasicOperationResult
import com.demn.plugincore.operation_result.OperationResult
import com.demn.plugincore.operation_result.TransitionOperationResult
import com.demn.pluginloading.MockOperationResultSorter
import com.demn.pluginloading.MockPluginRepository
import org.koin.androidx.compose.koinViewModel
import java.util.*

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    vm: SearchScreenViewModel = koinViewModel<SearchScreenViewModel>(),
) {
    val context = LocalContext.current
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadPlugins()
    }

    Column(modifier) {
        Spacer(modifier = Modifier.height(48.dp))

        SearchBar(
            searchBarValue = state.searchBarValue,
            onSearchBarValueChange = vm::updateSearchBarValue,
            onEnterClick = {
                val firstResult = state.searchResults.firstOrNull()

                firstResult?.let {
                    vm.executeResult(firstResult, context::startActivity)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        ResultList(
            results = state.searchResults,
            onResultClick = { vm.executeResult(it, context::startActivity) },
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (state.searchBarValue.isNotBlank()) {
            FallbackCommandsResultsList(
                currentInput = state.searchBarValue,
                fallbackCommands = state.fallbackCommands,
                onFallbackCommandClick = vm::invokeFallbackCommand
            )
        }
    }
}

@Composable
fun SearchBar(
    searchBarValue: String,
    onSearchBarValueChange: (String) -> Unit,
    onEnterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    TextField(
        value = searchBarValue,
        onValueChange = onSearchBarValueChange,
        placeholder = {
            Text(
                text = "Look for something…",
                style = MaterialTheme.typography.headlineMedium
            )
        },
        colors = TextFieldDefaults.colors(
            disabledTextColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(16.dp),
        textStyle = MaterialTheme.typography.headlineMedium,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Go
        ),
        keyboardActions = KeyboardActions(
            onGo = { onEnterClick() },
        ),
        maxLines = 1,
        minLines = 1,
        modifier = modifier
            .focusRequester(focusRequester)
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp)),
    )
}

@Composable
fun ResultList(
    results: List<OperationResult>,
    onResultClick: (OperationResult) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(results) { result ->
            if (result is BasicOperationResult) {
                BasicResult(
                    text = result.text,
                    onResultClick = { onResultClick(result) },
                )
            }

            if (result is TransitionOperationResult) {
                ConversionResult(
                    leftText = result.initialText,
                    leftLabel = result.initialDescription ?: "",
                    rightText = result.finalText,
                    rightLabel = result.finalDescription ?: "",
                )
            }
        }
    }
}

@Composable
fun BasicResult(
    text: String,
    onResultClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onResultClick() }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 8.dp
                )
        )
    }
}

@Composable
fun ConversionResult(
    leftText: String,
    leftLabel: String,
    rightText: String,
    rightLabel: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            Modifier
                .padding(16.dp)
        ) {
            Text(
                text = leftText,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .fillMaxWidth()
            )

            Label(leftLabel)

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = rightText,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .fillMaxWidth()
            )

            Label(rightLabel)
        }
    }
}

@Composable
fun Label(
    text: String,
    modifier: Modifier = Modifier
) {
    Column(
        Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.labelSmall,
            modifier = modifier
                .padding(4.dp),
        )
    }
}

@Composable
fun FallbackCommandsResultsList(
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
                onFallbackCommandClick = onFallbackCommandClick
            )
        }
    }
}

@Composable
fun FallbackCommandsResult(
    fallbackCommand: PluginFallbackCommand,
    onFallbackCommandClick: (id: UUID) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onFallbackCommandClick(fallbackCommand.id) },
        modifier = modifier
    ) {
        Text(
            text = fallbackCommand.name,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 8.dp
                )
        )

        val description = fallbackCommand.description
        if (description != null) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = 16.dp,
                        start = 16.dp,
                        end = 16.dp
                    )
            )
        }
    }
}

@Preview
@Composable
fun BasicResultPreview() {
    BasicResult(
        text = "help",
        onResultClick = {}
    )
}

@Preview
@Composable
fun ConversionResultPreview() {
    ConversionResult(
        leftText = "5$",
        leftLabel = "american dollar",
        rightText = "500₽",
        rightLabel = "russian rouble"
    )
}

@Preview
@Composable
fun SearchScreenPreview() {
    Surface(Modifier.fillMaxSize()) {
        SearchScreen(
            modifier = Modifier.fillMaxSize(),
            vm = SearchScreenViewModel(
                MockPluginRepository(),
                ProcessInputQueryUseCase(
                    MockPluginRepository(),
                    MockOperationResultSorter(),
                    MockPluginAvailabilityRepository(),
                    MockPluginCacheRepository()
                ),
                MockResultFrecencyRepository()
            ),
        )
    }
}