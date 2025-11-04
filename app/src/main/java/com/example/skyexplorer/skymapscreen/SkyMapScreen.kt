package com.example.skyexplorer.skymapscreen

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.webkit.WebView
import androidx.annotation.RequiresApi
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skyexplorer.components.BackwardButton
import com.example.skyexplorer.components.CameraButton
import com.example.skyexplorer.components.ForwardButton
import com.example.skyexplorer.components.InfoButton

@SuppressLint("SetJavaScriptEnabled", "RememberReturnType")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SkyMapScreen(
    viewModel: SkyMapViewModel,
    onNavigateToCamera: () -> Unit,
    onNavigateToConstellations: () -> Unit
) {
    val context = LocalContext.current


    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier.padding(20.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                CameraButton(onClick = {
                    viewModel.handleIntent(SkyMapIntent.NavigateToCamera)
                        ?.let { onNavigateToCamera() }
                }
                )

                InfoButton(onClick = {
                    viewModel.handleIntent(SkyMapIntent.NavigateToConstellations)
                        ?.let { onNavigateToConstellations() }
                })


            }
        }
        ){
        innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ){
                Text("SKY MAP", modifier = Modifier.padding(16.dp))

                val stars = remember {
                    context.assets.open("stars.json")
                        .bufferedReader()
                        .use { it.readText() }
                        .replace("NaN", "null") // Dodaj tę linię, aby zamienić NaN na null
                }
                StarMap(stars)
        }

    }

}




