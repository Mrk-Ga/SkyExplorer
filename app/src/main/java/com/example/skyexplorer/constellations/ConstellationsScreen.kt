package com.example.skyexplorer.constellations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.skyexplorer.components.BackwardButton
import com.example.skyexplorer.components.ConstellationItem
import com.example.skyexplorer.components.ForwardButton

@Composable
fun ConstellationsScreen(
    viewModel: ConstellationsViewModel,
    onGoBack: () -> Unit,
    onGoToPhotoGallery: () -> Unit
){

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier.padding(20.dp).fillMaxWidth() ,
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ){
                BackwardButton { onGoBack() }
                ForwardButton {
                    onGoToPhotoGallery()
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Text("CONSTELLATIONS", modifier = Modifier.padding(16.dp))

            ConstellationItem()
        }
    }


}
