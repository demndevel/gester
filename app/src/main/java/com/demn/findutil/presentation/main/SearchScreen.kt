package com.demn.findutil.presentation.main

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.demn.data.repo.MockResultFrecencyRepository
import com.demn.domain.models.PluginFallbackCommand
import com.demn.domain.usecase.MockCommandSearcherUseCase
import com.demn.domain.usecase.MockOperationResultSorterUseCase
import com.demn.domain.usecase.ProcessInputQueryUseCase
import com.demn.findutil.R
import com.demn.findutil.app_settings.MockPluginAvailabilityRepository
import com.demn.plugincore.operation_result.BasicOperationResult
import com.demn.plugincore.operation_result.CommandOperationResult
import com.demn.plugincore.operation_result.IconOperationResult
import com.demn.plugincore.operation_result.OperationResult
import com.demn.plugincore.operation_result.ResultType
import com.demn.plugincore.operation_result.TransitionOperationResult
import com.demn.pluginloading.MockPluginRepository
import org.koin.androidx.compose.koinViewModel
import java.util.UUID

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

@Composable
private fun SearchScreenContent(
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
            itemsIndexed(state.searchResults) { index, item ->
                ResultItem(
                    item,
                    index,
                    onResultClick = { vm.executeResult(it, context::startActivity) },
                    Modifier
                )
            }

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

@Composable
fun SearchBar(
    focusRequester: FocusRequester,
    searchBarValue: String,
    onSearchBarValueChange: (String) -> Unit,
    onEnterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    TextField(
        value = searchBarValue,
        onValueChange = onSearchBarValueChange,
        placeholder = {
            Text(
                text = "Look for something…",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        colors = TextFieldDefaults.colors(
            disabledTextColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(16.dp),
        textStyle = MaterialTheme.typography.headlineSmall,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Go
        ),
        keyboardActions = KeyboardActions(
            onGo = { onEnterClick() },
        ),
        trailingIcon = {
            IconButton(onClick = { onSearchBarValueChange("") }) {
                Icon(
                    Icons.Outlined.Clear,
                    null
                )
            }
        },
        maxLines = 1,
        minLines = 1,
        modifier = modifier
            .focusRequester(focusRequester)
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp))
            .onFocusChanged { if (it.hasFocus) keyboard?.show() },
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
            resultType = result.type
        )
    }

    if (result is IconOperationResult) {
        BasicResult(
            text = result.text,
            iconUri = result.iconUri,
            isFirst = index == 0,
            onResultClick = { onResultClick(result) },
            modifier = modifier.fillMaxWidth(),
            resultType = result.type
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
            text = result.name,
            iconUri = result.iconUri,
            onResultClick = { onResultClick(result) },
            isFirst = index == 0,
            modifier = modifier.fillMaxWidth(),
            resultType = result.type
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
    resultType: ResultType = ResultType.Other
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

                Spacer(Modifier.width(8.dp))
            }

            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .weight(1f)
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = getResultTypeText(resultType),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .border(
                        1.dp,
                        color = LocalContentColor.current,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(4.dp)
            )
        }
    }
}

@Composable
fun getResultTypeText(type: ResultType): String {
    return when (type) {
        ResultType.Command -> stringResource(R.string.command_result_type)

        ResultType.Alias -> stringResource(R.string.alias_result_type)

        ResultType.WebLink -> stringResource(R.string.weblink_result_type)

        ResultType.Information -> stringResource(R.string.information_result_type)

        ResultType.Application -> stringResource(R.string.application_result_type)

        ResultType.Other -> stringResource(R.string.other_result_type)

        else -> type.name
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun UriIcon(
    iconUri: Uri,
    modifier: Modifier = Modifier
) {
    val placeholderColor = MaterialTheme.colorScheme.primary.toArgb()
    GlideImage(
        model = iconUri,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
    ) {
        it
            .placeholder(ColorDrawable(placeholderColor))
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
                onFallbackCommandClick = onFallbackCommandClick,
                modifier = Modifier.fillMaxWidth()
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
        onClick = { onFallbackCommandClick(fallbackCommand.uuid) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            UriIcon(
                fallbackCommand.iconUri,
                Modifier
                    .size(32.dp)
                    .clip(CircleShape)
            )

            Text(
                text = fallbackCommand.name,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .fillMaxWidth()
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