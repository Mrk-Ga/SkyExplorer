package com.example.skyexplorer.camera

import androidx.annotation.Nullable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.unit.dp
import androidx.navigation.Navigation
import com.example.skyexplorer.components.ForwardButton
import kotlinx.serialization.descriptors.StructureKind

@Composable
fun CameraScreen(
    viewModel: CameraViewModel,
    onGoBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ){
                ForwardButton { onGoBack()}
            }
        }
    ){
        innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Text("CAMERA", modifier = Modifier.padding(16.dp))
        }
    }

}