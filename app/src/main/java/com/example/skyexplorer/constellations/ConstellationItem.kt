package com.example.skyexplorer.constellations

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.skyexplorer.data.photoFilesFormat

@Composable
fun ConstellationItem(
    mainContent: String,
    imageName: String,
    onClick: (constellationName: String) -> Unit
) {

    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .size(width = 200.dp, height = 200.dp),
        onClick = { onClick(imageName) }
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .align(alignment = Alignment.CenterHorizontally)
        ) {
            //val regex = Regex("file:///android_asset/constellationsPhotos/${imageName}\\d+\\.(png|jpg|jpeg)$", RegexOption.IGNORE_CASE)
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/constellationsPhotos/${imageName.replace(" ", "_")}.${photoFilesFormat}")
                    .build(),
                contentDescription = null,

                modifier = Modifier
                    .size(130.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.CenterHorizontally)

            ){
                Text(modifier = Modifier
                    .align(Alignment.Center),

                    text = mainContent,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }

        }
    }

}