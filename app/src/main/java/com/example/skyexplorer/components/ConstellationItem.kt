package com.example.skyexplorer.components

import android.graphics.pdf.models.ListItem
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.io.File

@Composable
fun ConstellationItem(
    mainContent: String,
    imageName: String
) {

    ListItem(


        modifier = Modifier
            .padding(10.dp)
            .clip(RoundedCornerShape(12.dp))

        //    .background(Color.Blue)
        ,
        leadingContent = {
            AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("file:///android_asset/$imageName")
                .build(),
            contentDescription = null,

            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        ) },
        headlineContent = {Text(text = mainContent)}
    )

}