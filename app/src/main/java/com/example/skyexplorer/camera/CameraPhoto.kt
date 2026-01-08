package com.example.skyexplorer.camera

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.skyexplorer.components.BouncyButton

@Composable
fun CameraPreview(viewModel: CameraViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val imageCapture = remember { ImageCapture.Builder().build() }

    val pendingPhoto = viewModel.pendingPhotoUri

    Box(modifier = Modifier.fillMaxSize()) {

        if (pendingPhoto == null) {
            // 📷 KAMERA
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.surfaceProvider = previewView.surfaceProvider
                        }

                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageCapture
                        )
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )

            CaptureButton(
                onClick = {viewModel.takePhoto(imageCapture, context){} },
                modifier = Modifier.align(Alignment.BottomCenter)
            )

        } else {
            AsyncImage(
                model = pendingPhoto,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            ConstellationChooseDialog(
                viewModel = viewModel,
                onDismissRequest = { viewModel.cancelPhoto() },
                onConfirmation = { constellation ->
                    viewModel.confirmConstellation(constellation)
                }
            )
        }
    }
}

@Composable
fun CaptureButton(onClick: () -> Unit, modifier: Modifier) {
    Box(
        modifier = modifier
            .padding(40.dp)
            .size(80.dp)

    ) {
        BouncyButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Filled.Circle,
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
        }
    }
}




