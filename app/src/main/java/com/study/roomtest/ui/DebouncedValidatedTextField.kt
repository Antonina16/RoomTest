package com.study.roomtest.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch


@OptIn(FlowPreview::class)
@Composable
fun DebouncedValidatedTextField(
    label: String,
    initialValue: String,
    validator: suspend (String) -> String?,
    onDone: (String) -> Unit,
    modifier: Modifier = Modifier,
    debounceMillis: Long = 500,
    keyboardOptions: KeyboardOptions  = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
) {
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    var text by rememberSaveable { mutableStateOf(initialValue) }
    var errorText by remember { mutableStateOf<String?>(null) }
    var hasFocus by remember { mutableStateOf(false) }

    LaunchedEffect(hasFocus) {
        if (hasFocus) {
            snapshotFlow { text }
                .debounce(debounceMillis)
//                .distinctUntilChanged()
                .collectLatest { currentText ->
                    errorText = validator(currentText)
                }
        }
    }

    OutlinedTextField(
        value = text,
        onValueChange = { newValue -> text = newValue },
        label = { Text(label) },
        isError = errorText != null,
        supportingText = {
            if (errorText != null)
                Text(errorText!!, color = MaterialTheme.colorScheme.error)
        },
        trailingIcon = {
            if (text != initialValue) {
                IconButton(onClick = {
                    text = initialValue
                    coroutineScope.launch {
                        errorText = validator(initialValue)
                    }
                    focusManager.clearFocus()
                }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset")
                }
            }
        },
        modifier = modifier.onFocusChanged { focusState ->
            val nowFocused = focusState.isFocused
            if (hasFocus && !nowFocused) {
                onDone(text)
            }
            hasFocus = nowFocused
        },
        keyboardOptions = keyboardOptions,
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() }
        )
    )
}

@Preview(showBackground = true)
@Composable
fun DebouncedValidatedTextFieldPreview() {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    fun validator(value: String): String? = when {
        value.isBlank()  -> "Name can't be blank"
        value.length < 3 -> "Too short"
        else             -> null
    }

    Column {
        DebouncedValidatedTextField(
            label = "First Name",
            initialValue = firstName,
            validator = ::validator,
            onDone = { firstName = it },
            modifier = Modifier.width(300.dp)
        )
        DebouncedValidatedTextField(
            label = "Last Name",
            initialValue = lastName,
            validator = ::validator,
            onDone = { lastName = it },
            modifier = Modifier.width(300.dp)
        )
    }
}
