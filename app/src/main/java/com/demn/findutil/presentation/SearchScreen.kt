package com.demn.findutil.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.demn.findutil.plugins.MockPluginRepository
import com.demn.findutil.usecase.MockProcessQueryUseCaseImpl
import com.demn.plugincore.operation_result.BasicOperationResult
import com.demn.plugincore.operation_result.OperationResult
import com.demn.plugincore.operation_result.TransitionOperationResult
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    vm: SearchScreenViewModel = koinViewModel<SearchScreenViewModel>(),
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadPlugins()
    }

    Column(modifier) {
        Spacer(modifier = Modifier.height(48.dp))

        SearchBar(
            searchBarValue = state.searchBarValue,
            onSearchBarValueChange = vm::updateSearchBarValue,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(Modifier) {
            items(state.pluginList) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(it.metadata.pluginUuid.toString())
                Text(it.metadata.pluginName)
            }
        }

        ResultList(
            results = state.searchResults,
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
        )
    }
}

@Composable
fun SearchBar(
    searchBarValue: String,
    onSearchBarValueChange: (String) -> Unit,
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
        modifier = modifier
            .focusRequester(focusRequester)
    )
}

@Composable
fun ResultList(
    results: List<OperationResult>,
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
                    description = result.description,
                    onResultClick = {
                    }
                )
            }

            if (result is TransitionOperationResult) {
                ConversionResult(
                    leftText = result.initialText,
                    leftLabel = result.initialDescription ?: "",
                    rightText = result.finalText,
                    rightLabel = result.finalDescription ?: ""
                )
            }
        }
    }
}

@Composable
fun BasicResult(
    text: String,
    description: String?,
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

            Divider()

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

@Preview
@Composable
fun BasicResultPreview() {
    BasicResult(
        text = "help",
        description = """
                    - type app name to launch
                    - type some math expression to calculate
                    - type some number with currency code to convert to your default currency (specified in the config)
                    - type "config" to show findutil config (aliases, plugins, etc.)
                """.trimIndent(),
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
            vm = SearchScreenViewModel(MockPluginRepository(), MockProcessQueryUseCaseImpl())
        )
    }
}