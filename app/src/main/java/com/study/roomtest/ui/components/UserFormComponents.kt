package com.study.roomtest.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.study.roomtest.viewmodels.UserField
import com.study.roomtest.viewmodels.UserState
import com.study.roomtest.viewmodels.UserViewModel

@Composable
fun UserInfoFields(uiState: UserState, userViewModel: UserViewModel) {
    Text("User Information", style = MaterialTheme.typography.headlineSmall)
    Spacer(modifier = Modifier.height(16.dp))

    ValidatedOutlinedTextField(
        value = uiState.firstName,
        onValueChange = { userViewModel.onFieldChange(it, UserField.FIRST_NAME) },
        label = "First Name",
        errorMessage = uiState.firstNameError
    )
    ValidatedOutlinedTextField(
        value = uiState.lastName,
        onValueChange = { userViewModel.onFieldChange(it, UserField.LAST_NAME) },
        label = "Last Name",
        errorMessage = uiState.lastNameError
    )
    ValidatedOutlinedTextField(
        value = uiState.emailAddress,
        onValueChange = { userViewModel.onFieldChange(it, UserField.EMAIL_ADDRESS) },
        label = "Email",
        errorMessage = uiState.lastNameError
    )
    ValidatedOutlinedTextField(
        value = uiState.phoneNumber,
        onValueChange = { userViewModel.onFieldChange(it, UserField.PHONE_NUMBER) },
        label = "PhoneNumber",
        errorMessage = uiState.phoneNumberError
    )
    // TODO: add rest of the fields
}

@Composable
fun HomeAddressFields(uiState: UserState, userViewModel: UserViewModel) {
    Text("Home Address", style = MaterialTheme.typography.headlineSmall)
    Spacer(modifier = Modifier.height(16.dp))
    ValidatedOutlinedTextField(
        value = uiState.city,
        onValueChange = { userViewModel.onFieldChange(it, UserField.CITY) },
        label = "City",
        errorMessage = null
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

