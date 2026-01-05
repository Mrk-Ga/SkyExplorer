package com.example.skyexplorer.constellations

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        bottomBar = {
            AppNavigationBar(
                onNavigateToCamera = onNavigateToCamera,
                onNavigateToConstellations = onNavigateToConstellations,
                onNavigateToSkyMap = onNavigateToSkyMap
            )

        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
            //    .background(Color.DarkGray)
            ,
        ) {

            //ConstellationItem()
            ConstellationsList(viewModel, viewModel.loadConstellationsInfo(),
                onNavigateToPhotoGallery)

        }
    }


}


