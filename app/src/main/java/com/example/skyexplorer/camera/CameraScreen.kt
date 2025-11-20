package com.example.skyexplorer.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.skyexplorer.components.BouncyButton
import com.example.skyexplorer.components.ForwardButton
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    viewModel: CameraViewModel,
    onGoBack: () -> Unit
) {
    val cameraPermission = rememberPermissionState(android.Manifest.permission.CAMERA)
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        cameraPermission.launchPermissionRequest()
    }

    //check if permission is granted
    if (cameraPermission.status.isGranted) {
        val state by viewModel.state.collectAsState()

        Box(modifier = Modifier.fillMaxSize()) {

            //compose with camera screen and photo button
            CameraPreview { uri ->
                viewModel.savePhoto(uri)
            }

            //row with "go to home screen" button
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(40.dp)
                    .background(Color.Transparent),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                BouncyButton(
                    onClick = onGoBack
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Go to main screen",
                        modifier = Modifier
                            .size(50.dp)
                            //.padding(30.dp)
                    )
                }
            }
        }
    } else {
        //in case of permission denied
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Wymagane pozwolenie na użycie kamery")
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
                    .background(Color.Transparent),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                ForwardButton { onGoBack() }
            }
        }
    }

}