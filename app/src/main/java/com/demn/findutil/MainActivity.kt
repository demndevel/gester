package com.demn.findutil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.demn.findutil.di.appModule
import com.demn.findutil.models.Application
import com.demn.findutil.ui.theme.FindUtilTheme
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.compose.koinViewModel
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startKoin {
            androidContext(applicationContext)
            modules(listOf(appModule))
        }

        setContent {
            FindUtilTheme {
                SearchScreen(Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier
) {
    val vm = koinViewModel<SearchScreenViewModel>()
    val state by vm.state.collectAsState()

    Column(modifier) {
        Spacer(modifier = Modifier.height(48.dp))

        SearchBar(
            searchBarValue = state.searchBarValue,
            onSearchBarValueChange = vm::updateSearchBarValue,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedVisibility(visible = state.searchBarValue.isNotEmpty()) {
            SuggestionList(
                suggestions = state.foundApplications,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SearchBar(
    searchBarValue: String,
    onSearchBarValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
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
    )
}

@Composable
fun SuggestionList(
    suggestions: List<Application>,
    modifier: Modifier = Modifier
) {
    val ctx = LocalContext.current

    LazyColumn(modifier) {
        items(suggestions) { suggestion ->
            Suggestion(
                text = suggestion.name,
                onSuggestionClick = {
                    if (suggestion.intentToLaunch != null) {
                        ctx.startActivity(suggestion.intentToLaunch)
                    }
                }
            )

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun Suggestion(
    text: String,
    onSuggestionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onSuggestionClick() }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FindUtilTheme {
        SearchScreen(Modifier.fillMaxSize())
    }
}