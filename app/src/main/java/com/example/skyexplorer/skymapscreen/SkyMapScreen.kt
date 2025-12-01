package com.example.skyexplorer.skymapscreen

import SkyMapViewModel
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.pdf.content.PdfPageGotoLinkContent
import android.os.Build
import android.util.Log
import android.webkit.WebView
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skyexplorer.components.BackwardButton
import com.example.skyexplorer.components.BouncyButton
import com.example.skyexplorer.components.CameraButton
import com.example.skyexplorer.components.ForwardButton
import com.example.skyexplorer.components.InfoButton


@SuppressLint("SetJavaScriptEnabled", "RememberReturnType")
@RequiresApi(Build.VERSION_CODES.O)
@androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION])
@Composable
fun SkyMapScreen(
    viewModel: SkyMapViewModel,
    onNavigateToCamera: () -> Unit,
    onNavigateToConstellations: () -> Unit
) {
    val context = LocalContext.current

    val permissionFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
    val permissionCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)

    if (permissionFine != PackageManager.PERMISSION_GRANTED &&
        permissionCoarse != PackageManager.PERMISSION_GRANTED) {
        Text("⚠️ Brak uprawnień lokalizacji")
        return
    }
    val stars by viewModel.stars.collectAsState()
    LaunchedEffect(Unit) {
        Log.d("DEBUG", "LaunchedEffect start – uruchamiam viewModel.loadStars()")
        viewModel.loadStars()
    }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            com.example.skyexplorer.components.NavigationBar(
                onNavigateToCamera = onNavigateToCamera,
                onNavigateToConstellations = onNavigateToConstellations
            )
            /*
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        onNavigateToCamera()
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        //
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = null,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        onNavigateToConstellations()
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                )

            }

             */
            /*
            Box(

                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp) // stała wysokość bottom bara
                    .padding(horizontal = 40.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    BouncyButton(onClick = { onNavigateToCamera() }) {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.size(50.dp)
                        )
                    }

                    BouncyButton(onClick = { onNavigateToConstellations() }) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }
            }

             */
        }
    ){
            innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )  {
            Text("SKY MAP", modifier = Modifier.padding(16.dp))



            if (stars.isEmpty()) {
                Text("Wczytywanie nieba...")
            } else {
                StarMap(stars)
            }


        }

    }

}