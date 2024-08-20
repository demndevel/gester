package com.demn.findutil.presentation.main.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

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