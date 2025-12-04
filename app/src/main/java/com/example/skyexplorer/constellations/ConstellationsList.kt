package com.example.skyexplorer.constellations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import com.example.skyexplorer.components.ConstellationInfo
import com.example.skyexplorer.components.ConstellationItem


@Composable
fun ConstellationsList(
    viewModel: ConstellationsViewModel,
    items: List<ConstellationInfo>,
    onConstellationChoose: (constellationName: String)->Unit
) {

    val photos by viewModel.photos.collectAsState()

    LazyVerticalGrid(
        contentPadding = PaddingValues(15.dp),
        columns = GridCells.Adaptive(minSize = 128.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)


    ) {
        items(items) { item ->


            ConstellationItem(
                mainContent = item.polish,
                imageName = item.latin,
                onClick = { constellationName ->
                    onConstellationChoose(constellationName)
                }
            )

        }
    }


}
