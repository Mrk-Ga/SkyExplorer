package com.example.skyexplorer.photoGallery

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.skyexplorer.PhotoEntity

@Composable
fun PhotoPager(
    photos: List<PhotoEntity>,
    constellationName: String
) {
    if (photos.isEmpty()) {
        // Placeholder with specific constellation

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("file:///android_asset/constellationsPhotos/${constellationName.replace(" ", "_")}.jpg")
                .build(),
            contentDescription = null,

            modifier = Modifier
                //clip(RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Crop,
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
                .padding(horizontal = 16.dp),
            contentScale = ContentScale.Crop
        )
    }
}