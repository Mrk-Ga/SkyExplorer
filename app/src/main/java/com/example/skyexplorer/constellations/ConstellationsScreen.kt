package com.example.skyexplorer.constellations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.skyexplorer.components.AppNavigationBar

@Composable
fun ConstellationsScreen(
    viewModel: ConstellationsViewModel,
    onNavigateToSkyMap: () -> Unit,
    onNavigateToConstellations: () -> Unit,
    onNavigateToCamera: () -> Unit,
    onNavigateToPhotoGallery: (constellationName: String) -> Unit
){



    Scaffold(
        contentWindowInsets = WindowInsets(0),
        containerColor = Color.Transparent,//MaterialTheme.colorScheme.background,
        bottomBar = {

            AppNavigationBar(
                onNavigateToCamera = onNavigateToCamera,
                onNavigateToConstellations = onNavigateToConstellations,
                onNavigateToSkyMap = onNavigateToSkyMap
            )

        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF050814)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(
                        color = Color(0xFF0B1025).copy(alpha = 0.7f),
                        shape = RoundedCornerShape(24.dp),
                    )

            ) {


                //ConstellationItem()
                ConstellationsList(
                    viewModel, viewModel.loadConstellationsInfo(),
                    onNavigateToPhotoGallery
                )


            }
        }
    }


}


