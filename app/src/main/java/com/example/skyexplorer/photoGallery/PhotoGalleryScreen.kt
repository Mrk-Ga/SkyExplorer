package com.example.skyexplorer.photoGallery

import android.annotation.SuppressLint
import android.media.Image
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skyexplorer.camera.CameraViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.skyexplorer.components.BouncyButton


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PhotoGalleryScreen(
    viewModel: PhotoGalleryViewModel = viewModel(),
    onNavigateToConstellations: () -> Unit
) {
    val photos by viewModel.photos.collectAsState()

    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ){
                BouncyButton(
                    onClick = onNavigateToConstellations
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )


                }
            }
        }
    ) { innerPadding ->

        LazyColumn {
            items(photos) { photo ->
                ElevatedCard(
                    onClick = { viewModel.deletePhoto(photo) }
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(photo.uri),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}


