package com.example.skyexplorer.constellations

import android.graphics.pdf.models.ListItem
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AddToHomeScreen
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.skyexplorer.components.BackwardButton
import com.example.skyexplorer.components.BouncyButton
import com.example.skyexplorer.components.ConstellationItem
import com.example.skyexplorer.components.ForwardButton

@Composable
fun ConstellationsScreen(
    viewModel: ConstellationsViewModel,
    onGoBack: () -> Unit,
    onGoToPhotoGallery: () -> Unit,
    onNavigateToCamera: () -> Unit
){

    Scaffold(
        bottomBar = {

                //BackwardButton { onGoBack() }
            /*
            com.example.skyexplorer.components.NavigationBar(
                onNavigateToCamera = onNavigateToCamera,
                onNavigateToConstellations =
            )

             */
                /*
                BouncyButton(
                    onClick = onGoBack
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go to previous screen",
                        modifier = Modifier.size(70.dp)
                    )
                }
                BouncyButton(
                    onClick = onGoToPhotoGallery
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Go to photo gallery",
                        modifier = Modifier.size(70.dp)
                    )
                }

                 */

        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
            //    .background(Color.DarkGray)
            ,
        ) {
            Text("CONSTELLATIONS", modifier = Modifier.padding(16.dp))

            //ConstellationItem()
            ConstellationsList(items, onGoToPhotoGallery)

        }
    }


}

data class Item(val name: String, val imageName: String)

val items = listOf(
    Item("Pierwszy", "andromeda_constellation.jpg"),
    Item("Drugi", "andromeda_constellation.jpg"),
    Item("Trzeci", "andromeda_constellation.jpg"),
    Item("Pierwszy", "andromeda_constellation.jpg"),
    Item("Drugi", "andromeda_constellation.jpg"),
    Item("Trzeci", "andromeda_constellation.jpg"),
    Item("Pierwszy", "andromeda_constellation.jpg"),
    Item("Drugi", "andromeda_constellation.jpg"),
    Item("Trzeci", "andromeda_constellation.jpg")
)
@Composable
fun ConstellationsList(
    items: List<Item>,
    onClickTEST: ()->Unit
) {

    LazyVerticalGrid(
        contentPadding = PaddingValues(15.dp),
        columns = GridCells.Adaptive(minSize = 128.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)


    ) {
        items(items) { item ->
            ConstellationItem(
                mainContent = item.name,
                imageName = item.imageName,
                onClick = onClickTEST
            )

        }
    }
    /*
    LazyVerticalGrid {
        items(items) { item ->
            ConstellationItem(
                mainContent = item.name,
                imageName = item.imageName
            )
        }
    }

     */
}

