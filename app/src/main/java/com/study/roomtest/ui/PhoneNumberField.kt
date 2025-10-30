package com.study.roomtest.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class PhoneMaskTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text.take(9)
        val out = buildString {
            append("+38 (0")
            append(raw.take(2).padEnd(2, '_'))
            append(") ")
            append(raw.drop(2).take(3).padEnd(3, '_'))
            append('-')
            append(raw.drop(5).take(2).padEnd(2, '_'))
            append('-')
            append(raw.drop(7).take(2).padEnd(2, '_'))
        }

        val map = object : OffsetMapping {
            // raw (0..9) -> masked (0..19)
            override fun originalToTransformed(offset: Int): Int = when {
                offset <= 0 -> 6                          // +38 (0|
                offset <= 2 -> 6 + offset                 // +38 (0XX|
                offset <= 5 -> 10 + (offset - 2)          // +38 (0XX) XXX|
                offset <= 7 -> 14 + (offset - 5)          // +38 (0XX) XXX-XX|
                else        -> 17 + (offset - 7)          // +38 (0XX) XXX-XX-XX|
            }.coerceAtMost(out.length)

            // masked (0..19) -> raw (0..9)
            override fun transformedToOriginal(offset: Int): Int = when {
                offset <= 6  -> 0                         // до первых цифр
                offset <= 8  -> offset - 6                // в зоне первых 2 цифр
                offset <= 10 -> 2                         // внутри ") "
                offset <= 13 -> offset - 8                // 3 цифры после ") "
                offset <= 14 -> 5                         // внутри первого '-'
                offset <= 16 -> offset - 9                // 2 цифры после первого '-'
                offset <= 17 -> 7                         // внутри второго '-'
                else         -> offset - 10               // 2 последние цифры
            }.coerceIn(0, raw.length)
        }


        return TransformedText(AnnotatedString(out), map)
    }
}

@Composable
fun PhoneNumberField(
    modifier: Modifier = Modifier,
    initialValue: String,
    onNumberComplete: (String) -> Unit = {},
    debounceMillis: Long = 500,
    validator: suspend (String) -> String?
) {
    var rawNumber by rememberSaveable { mutableStateOf(normalizeToRaw(initialValue)) }         // только 9 цифр без ведущего 0
    var errorText by remember { mutableStateOf<String?>(null) }
    var hasFocus by remember { mutableStateOf(false) }
    val maxRaw = 9


    val isComplete = rawNumber.length == maxRaw

    LaunchedEffect(hasFocus) {
        if (hasFocus) {
            snapshotFlow { rawNumber }
                .debounce(debounceMillis)
                .collectLatest { current ->
                    errorText = validator(current)
                }
        }
    }

    LaunchedEffect(isComplete, hasFocus) {
        if (!hasFocus) {
            errorText = validator(rawNumber)
        }
    }

    OutlinedTextField(
        value = rawNumber,
        onValueChange = { newValue ->
            val nextRaw = normalizeToRaw(newValue)
            if (nextRaw != rawNumber) {
                rawNumber = nextRaw
                if (nextRaw.length == maxRaw) {
                    onNumberComplete("0$nextRaw")
                }
            }
        },
        label = { Text("Phone number") },
        isError = errorText != null,
        supportingText = {
            errorText?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        },
        visualTransformation = PhoneMaskTransformation() ,
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { hasFocus = it.isFocused },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = LocalTextStyle.current.copy(letterSpacing = 1.5.sp),
        singleLine = true
    )
}
private fun normalizeToRaw(input: String): String {
    var d = input.filter { it.isDigit() }
    d = when {
        d.startsWith("380") -> "0" + d.drop(3)
        d.startsWith("38")  -> "0" + d.drop(2)
        else                -> d
    }
    if (d.startsWith("0")) d = d.drop(1)
    return d.take(9)
}

@Preview(showBackground = true)
@Composable
fun PhoneFormDemo() {
    var result by remember { mutableStateOf("") }
    Column(
        Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        PhoneNumberField(
            initialValue = "",
            onNumberComplete = { phone -> result = phone },
            validator = { raw ->
                when {
                    raw.isBlank() -> "Phone is required"
                    raw.length < 9 -> "Incomplete phone number"
                    else -> null
                }
            }
        )
        if (result.isNotBlank()) {
            Text(result)
        }
    }
}
