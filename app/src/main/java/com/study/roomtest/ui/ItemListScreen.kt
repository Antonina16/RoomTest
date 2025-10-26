package com.study.roomtest.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.study.roomtest.viewmodels.ItemViewModel
import com.study.roomtest.viewmodels.UserViewModel

@Composable
fun ItemListScreen(
    modifier: Modifier = Modifier,
    viewModel: UserViewModel = viewModel()
) {
    val users by viewModel.hotFlow.collectAsState(initial = emptyList())
    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .then(modifier)
    ) {
        items(users) { user ->
            Text("• ${user.firstName} ${user.lastName} ${user.emailAddress}",
                style = MaterialTheme.typography.bodyLarge)
        }
    }
}

