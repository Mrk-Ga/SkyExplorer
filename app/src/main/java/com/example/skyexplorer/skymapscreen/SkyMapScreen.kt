package com.example.skyexplorer.skymapscreen

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.skyexplorer.components.AppNavigationBar
import com.example.skyexplorer.data.localizationPermissionText
import com.example.skyexplorer.data.mapRenderingInfo

@SuppressLint("SetJavaScriptEnabled", "RememberReturnType")
@RequiresApi(Build.VERSION_CODES.O)
@androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION])
@Composable
fun SkyMapScreen(
    viewModel: SkyMapViewModel,
    onNavigateToCamera: () -> Unit,
    onNavigateToConstellations: () -> Unit,
    onNavigateToSkyMap: () -> Unit
) {
    val context = LocalContext.current

    val permissionFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
    val permissionCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)

    if (permissionFine != PackageManager.PERMISSION_GRANTED &&
        permissionCoarse != PackageManager.PERMISSION_GRANTED) {
        Text(localizationPermissionText)
        return
    }
    val stars by viewModel.stars.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("DEBUG", "LaunchedEffect start – uruchamiam viewModel.loadStars()")
        viewModel.loadStars()
    }



    val constellations by viewModel.constellations.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.loadConstellations()
        Log.d("SCREEN LOG", constellations.size.toString())
    }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {

            AppNavigationBar(
                onNavigateToCamera = onNavigateToCamera,
                onNavigateToConstellations = onNavigateToConstellations,
                onNavigateToSkyMap = onNavigateToSkyMap
            )
        }
    ){
            innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )  {



            if (stars.isEmpty()) {
                Text(mapRenderingInfo, modifier = Modifier.padding(100.dp))
            } else {
                StarMap(stars, viewModel,constellations)
            }


        }

    }

}
