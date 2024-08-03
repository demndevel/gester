package com.demn.findutil.presentation.main

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.demn.data.repo.MockResultFrecencyRepository
import com.demn.domain.usecase.MockCommandSearcherUseCase
import com.demn.domain.usecase.ProcessInputQueryUseCase
import com.demn.domain.usecase.MockOperationResultSorterUseCase
import com.demn.findutil.R
import com.demn.findutil.app_settings.MockPluginAvailabilityRepository
import com.demn.plugincore.PluginFallbackCommand
import com.demn.plugincore.operation_result.BasicOperationResult
import com.demn.plugincore.operation_result.CommandOperationResult
import com.demn.plugincore.operation_result.IconOperationResult
import com.demn.plugincore.operation_result.OperationResult
import com.demn.plugincore.operation_result.TransitionOperationResult
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

    LazyColumn(
        modifier
    ) {
        item {
            Spacer(modifier = Modifier.height(48.dp))

            SearchBar(
                searchBarValue = vm.searchBarState,
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
        }

        itemsIndexed(state.searchResults) { index, item ->
            ResultItem(
                item,
                index,
                onResultClick = { vm.executeResult(it, context::startActivity) },
                Modifier
            )
        }

        if (vm.searchBarState.isNotBlank()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                FallbackCommandsResultsList(
                    currentInput = vm.searchBarState,
                    fallbackCommands = state.fallbackCommands,
                    onFallbackCommandClick = vm::invokeFallbackCommand
                )
            }
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
fun ResultItem(
    result: OperationResult,
    index: Int,
    onResultClick: (OperationResult) -> Unit,
    modifier: Modifier = Modifier
) {
    if (result is BasicOperationResult) {
        BasicResult(
            text = result.text,
            onResultClick = { onResultClick(result) },
            isFirst = index == 0,
            modifier = modifier.fillMaxWidth(),
        )
    }

    if (result is IconOperationResult) {
        BasicResult(
            text = result.text,
            iconUri = result.iconUri,
            isFirst = index == 0,
            onResultClick = { onResultClick(result) },
            modifier = modifier.fillMaxWidth()
        )
    }

    if (result is TransitionOperationResult) {
        ConversionResult(
            leftText = result.initialText,
            leftLabel = result.initialDescription ?: "",
            rightText = result.finalText,
            rightLabel = result.finalDescription ?: "",
            modifier = modifier.fillMaxWidth()
        )
    }

    if (result is CommandOperationResult) {
        BasicResult(
            text = "[command] ${result.name}",
            onResultClick = { onResultClick(result) },
            isFirst = index == 0,
            modifier = modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun BasicResult(
    text: String,
    onResultClick: () -> Unit,
    isFirst: Boolean,
    modifier: Modifier = Modifier,
    iconUri: Uri? = null,
) {
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = when (isFirst) {
                true -> 6.dp
                false -> 1.dp
            },
            pressedElevation = 12.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = when (isFirst) {
                true -> MaterialTheme.colorScheme.primaryContainer
                false -> MaterialTheme.colorScheme.surface
            }
        ),
        modifier = modifier
            .clickable { onResultClick() },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
        ) {
            iconUri?.let {
                UriIcon(
                    iconUri = it,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                )
            }

            Spacer(Modifier.width(8.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .weight(1f)
            )
        }
    }
}

@Composable
private fun UriIcon(
    iconUri: Uri,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        ImageRequest.Builder(LocalContext.current)
            .data(iconUri)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
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
            .height(IntrinsicSize.Min),
    ) {
        Row(
            Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
            ) {
                Text(
                    text = leftText,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                )

                Spacer(Modifier.height(8.dp))

                Label(leftLabel)
            }

            Spacer(modifier = Modifier.width(8.dp))

            VerticalDivider(Modifier) // here

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
            ) {
                Text(
                    text = rightText,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                )

                Spacer(Modifier.height(8.dp))

                Label(rightLabel)
            }
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
fun BasicResultNotFirstPreview() {
    BasicResult(
        text = "help",
        onResultClick = {},
        false,
        iconUri = null,
    )
}

@Preview
@Composable
fun BasicResultFirstPreview() {
    BasicResult(
        text = "help",
        onResultClick = {},
        true,
        iconUri = null,
    )
}

@Preview
@Composable
fun ConversionResultPreview() {
    Box(Modifier.fillMaxSize()) {
        ConversionResult(
            leftText = "5$",
            leftLabel = "american dollar",
            rightText = "500₽",
            rightLabel = "russian rouble",
//        modifier = Modifier.height(100.dp)
        )
    }
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
                    MockOperationResultSorterUseCase(),
                    MockPluginAvailabilityRepository(),
                    MockCommandSearcherUseCase()
                ),
                MockResultFrecencyRepository()
            ),
        )
    }
}