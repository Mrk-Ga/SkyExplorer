package com.example.skyexplorer.photoGallery

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.skyexplorer.camera.CameraViewModel
import coil.compose.rememberAsyncImagePainter



@Composable
fun PhotoGalleryScreen(
    viewModel: PhotoGalleryViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val photos by viewModel.photos.collectAsState()

    LazyColumn {
        items(photos) { photo ->
            ElevatedCard(
                onClick = {viewModel.deletePhoto(photo)}
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


