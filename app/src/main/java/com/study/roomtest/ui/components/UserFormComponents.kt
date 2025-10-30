package com.study.roomtest.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.study.roomtest.ui.DebouncedValidatedTextField
import com.study.roomtest.ui.PhoneNumberField
import com.study.roomtest.viewmodels.UserField
import com.study.roomtest.viewmodels.UserState
import com.study.roomtest.viewmodels.UserViewModel

@Composable
fun UserInfoFields(uiState: UserState, userViewModel: UserViewModel) {
    Text("User Information", style = MaterialTheme.typography.headlineSmall)
    Spacer(modifier = Modifier.height(16.dp))

    DebouncedValidatedTextField(
        label = "First Name",
        initialValue = uiState.firstName,
        validator = { userViewModel.validateField(UserField.FIRST_NAME, it) },
        onDone = { userViewModel.onFieldChange(it, UserField.FIRST_NAME) },
        modifier = Modifier.width(300.dp),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
    )
    DebouncedValidatedTextField(
        label = "Last Name",
        initialValue = uiState.lastName,
        validator = { userViewModel.validateField(UserField.LAST_NAME, it) },
        onDone = { userViewModel.onFieldChange(it, UserField.LAST_NAME) },
        modifier = Modifier.width(300.dp),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
    )
    DebouncedValidatedTextField(
        label = "Email",
        initialValue = uiState.emailAddress,
        validator = { userViewModel.validateField(UserField.EMAIL_ADDRESS, it) },
        onDone = { userViewModel.onFieldChange(it, UserField.EMAIL_ADDRESS) },
        modifier = Modifier.width(300.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        )
    )
    PhoneNumberField(
        initialValue = uiState.phoneNumber,
        onNumberComplete = { phone ->
            userViewModel.onFieldChange(phone, UserField.PHONE_NUMBER)
        },
        validator = { userViewModel.validateField( UserField.PHONE_NUMBER, it,) },
        modifier = Modifier.width(300.dp)

    )
    // TODO: add rest of the fields
}

@Composable
fun HomeAddressFields(uiState: UserState, userViewModel: UserViewModel) {
    Text("Home Address", style = MaterialTheme.typography.headlineSmall)
    Spacer(modifier = Modifier.height(16.dp))

    DebouncedValidatedTextField(
        label = "City",
        initialValue = uiState.city,
        validator = { userViewModel.validateField(UserField.CITY, it) },
        onDone = { userViewModel.onFieldChange(it, UserField.CITY) },
        modifier = Modifier.width(300.dp),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
    )

    // TODO: rest of the Address fields
    // ... OutlinedTextFields for all address fields
}

@Composable
fun ValidatedOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    errorMessage: String?,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = errorMessage != null,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = keyboardOptions
    )
    errorMessage?.let {
        Text(
            text = it,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
}

