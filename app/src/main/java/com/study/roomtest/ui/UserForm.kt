package com.study.roomtest.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.study.roomtest.room.user.HomeAddress
import com.study.roomtest.room.user.User
import com.study.roomtest.ui.components.HomeAddressFields
import com.study.roomtest.ui.components.UserInfoFields
import com.study.roomtest.viewmodels.UserViewModel

@Composable
fun UserForm(
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = viewModel(),
) {
    val uiState by userViewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val isSaveEnabled by userViewModel.isSaveEnabled.collectAsState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .then(modifier)
    ) {
        if (isLandscape) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    UserInfoFields(uiState, userViewModel)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    HomeAddressFields(uiState, userViewModel)
                }
            }
        } else {
            UserInfoFields(uiState, userViewModel)
            Spacer(modifier = Modifier.height(16.dp))
            HomeAddressFields(uiState, userViewModel)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { userViewModel.saveUser() },
            modifier = Modifier.fillMaxWidth(),
            enabled = isSaveEnabled

        ) {
            Text("Save User")
        }
    }
}