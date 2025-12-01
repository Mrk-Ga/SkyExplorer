package com.example.skyexplorer.camera

import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.skyexplorer.components.BouncyButton
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun CameraPreview(onImageCaptured: (Uri) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val imageCapture = remember { ImageCapture.Builder().build() }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { ctx ->
            val previewView = androidx.camera.view.PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, preview, imageCapture
                    )
                } catch (exc: Exception) {
                    exc.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
            modifier = Modifier.fillMaxSize()
        )

        Box (
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.BottomCenter)
                .padding(40.dp)
                .size(80.dp),
        ){

            BouncyButton(

                onClick = {
                    //
                    val photoFile = File(
                        context.externalMediaDirs.first(),
                        SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US)
                            .format(System.currentTimeMillis()) + ".jpg"
                    )

                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                    imageCapture.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onError(exc: ImageCaptureException) {
                                exc.printStackTrace()
                            }

                            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                onImageCaptured(Uri.fromFile(photoFile))
                            }

                        }
                    )

                },
                /*
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.BottomCenter)
                .padding(40.dp)
                .size(70.dp),
            contentColor = Color.White,
            shape = RoundedCornerShape(50),

             */


            ) {
                Icon(
                    imageVector = Icons.Filled.Circle,
                    contentDescription = "Take photo",
                    modifier = Modifier.size(80.dp)
                )
            }
        }
    }
}


