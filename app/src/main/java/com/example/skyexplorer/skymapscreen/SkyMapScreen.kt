package com.example.skyexplorer.skymapscreen

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
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
    val state by viewModel.state.collectAsState()



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
                /*val stars = remember {
                    viewModel.loadStars(context)
                }*/
                // uproszczenie do rysowania gwiazd
                val stars = viewModel.loadStars(context).mapIndexed { index, star ->
                    star.copy(
                        azimuth = (index * 45.0) % 360.0, // rozkład co 45 stopni
                        altitude = (30 + (index * 10) % 60).toDouble() // wysokość 30–90°
                    )
                }
                val constellations = remember {
                    viewModel.loadConstellations(context)
                }

                SkyMapView(stars, constellations)

        }

    }
    /*

    val uiState by viewModel.uiState.collectAsState()
    val hasPermission = uiState.hasPermission
    val webUrl = uiState.webUrl

    if (!hasPermission) {
        // Użytkownik nie dał jeszcze zgody – wyślij intencję
        viewModel.handleIntent(SkyMapIntent.RequestNavigationPermission)
    } else {
        // Mapa nieba
        AndroidView(factory = {
            WebView(it).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                loadUrl(webUrl)
            }
        }, update = {
            it.loadUrl(webUrl)
        })
    }

    // Efekt pobierania lokalizacji
    LaunchedEffect(Unit) {
        viewModel.fetchCurrentLocation(context) { lat, lon ->
            val time = java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC)
                .toString().substringBeforeLast("[")
            val webUrlLocation = "https://stellarium-web.org/?lat=$lat&lon=$lon&date=$time&fov=100"
        }
    }
    */

}




