package com.example.skyexplorer.camera

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CameraScreen(
    viewModel: CameraViewModel,
    onGoBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Text(text = "Camera")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Button(onClick = { onGoBack() }) {
            Text("Powrót")
        }
    }
}