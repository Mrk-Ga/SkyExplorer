package com.example.skyexplorer.photoGallery

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.skyexplorer.PhotoEntity
import com.example.skyexplorer.ui.theme.DarkGray

@Composable
fun PhotoPager(
    photos: List<PhotoEntity>,
    constellationName: String,
    onDeletePhoto: (PhotoEntity) ->Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedPhoto by remember { mutableStateOf<PhotoEntity?>(null) }

    // Dialog potwierdzający usunięcie
    if (showDialog && selectedPhoto != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Text(
                    "Usuń",
                    color = Color.Red,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            onDeletePhoto(selectedPhoto!!)
                            showDialog = false
                        }
                )
            },
            dismissButton = {
                Text(
                    "Anuluj",
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { showDialog = false },
                    color = DarkGray
                )
            },
            title = { Text("Usunąć zdjęcie?") },
            text = { Text("Czy na pewno chcesz usunąć to zdjęcie?") }
        )
    }



    if (photos.isEmpty()) {

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("file:///android_asset/constellationsPhotos/${constellationName.replace(" ", "_")}.jpg")
                .size(512)
                .build(),
            contentDescription = null,

            modifier = Modifier
                //clip(RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(300.dp),
            contentScale = ContentScale.Fit,
            alignment = Alignment.Center
        )

        return
    }

    // Pager state
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { photos.size }
    )

    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp), // Możesz dostosować
        pageSpacing = 16.dp
    ) { page ->

        val photo = photos[page]



        Image(
            painter = rememberAsyncImagePainter(photo.uri),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            selectedPhoto = photo
                            showDialog = true
                        }
                    )
                },
            contentScale = ContentScale.Crop
        )
    }
}


