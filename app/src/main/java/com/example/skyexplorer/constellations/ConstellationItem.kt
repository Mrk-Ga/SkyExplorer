package com.example.skyexplorer.constellations

import android.R.attr.enabled
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.skyexplorer.data.photoFilesFormat
import com.example.skyexplorer.ui.theme.Black
import com.example.skyexplorer.ui.theme.DarkGray
import com.example.skyexplorer.ui.theme.StarBlue
import com.example.skyexplorer.ui.theme.StarDark
import com.example.skyexplorer.ui.theme.StarLightBlue

@Composable
fun ConstellationItem(
    mainContent: String,
    imageName: String,
    onClick: (constellationName: String) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        label = "scale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 20.dp,
        label = "elevation"
    )

    ElevatedCard(
        onClick = { onClick(imageName) },
        interactionSource = interactionSource,
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation
        ),
        modifier = Modifier
            .size(200.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkGray.copy(alpha = 0.7f)
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.padding(10.dp).align(Alignment.Center)
            ) {

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(
                            "file:///android_asset/constellationsPhotos/${
                                imageName.replace(
                                    " ",
                                    "_"
                                )
                            }.${photoFilesFormat}"
                        )
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(130.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .align(Alignment.CenterHorizontally),
                    contentScale = ContentScale.Crop
                )

                Text(
                    text = mainContent,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }

            if (isPressed) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Color.Black.copy(alpha = 0.15f),
                            RoundedCornerShape(24.dp)
                        )
                )
            }
        }
    }
}

@Preview
@Composable
fun ConstItemPreview(

){
    ConstellationItem(
        mainContent = "Andromeda",
        imageName = "Andromeda",
        onClick = {}
    )
}

