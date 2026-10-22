
package com.example.ca2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

// VIEWMODEL: Produces one-time notification events
class MessengerViewModel : ViewModel() {

    private val _notificationEvent = MutableSharedFlow<String>(
        replay = 0,
        extraBufferCapacity = 1
    )

    val notificationEvent = _notificationEvent.asSharedFlow()

    fun sendNotification(message: String) {
        viewModelScope.launch {
            _notificationEvent.emit(message)
        }
    }

    fun simulateNetworkError() {
        sendNotification("Network Connection Lost")
    }

    fun simulatePasswordUpdate() {
        sendNotification("Password Updated Successfully")
    }
}

class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                MessengerScreen()
            }
        }
    }
}

@Composable
fun MessengerScreen(
    viewModel: MessengerViewModel = viewModel()
) {
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    // Collect one-time events while this UI is active
    LaunchedEffect(viewModel) {
        viewModel.notificationEvent.collect { message ->
            snackbarHostState.showSnackbar(
                message = message,
                withDismissAction = true,
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Button(
                onClick = {
                    viewModel.simulateNetworkError()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simulate Network Error")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.simulatePasswordUpdate()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simulate Password Update")
            }
        }
    }
}
