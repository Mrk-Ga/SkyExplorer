package com.example.skyexplorer


import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.skyexplorer.camera.CameraScreen
import com.example.skyexplorer.camera.CameraViewModel
import com.example.skyexplorer.constellations.ConstellationsScreen
import com.example.skyexplorer.constellations.ConstellationsViewModel
import com.example.skyexplorer.skymapscreen.*

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "skymap") {

        composable("skymap") {
            val skyMapViewModel: SkyMapViewModel = viewModel()
            SkyMapScreen(
                viewModel = skyMapViewModel,
                onNavigateToCamera = { navController.navigate("camera") },
                onNavigateToConstellations = { navController.navigate("constellations") }
            )
        }

        composable("camera") {
            val cameraViewModel: CameraViewModel = viewModel()
            CameraScreen(
                viewModel = cameraViewModel,
                onGoBack = { navController.popBackStack() }
            )
        }

        composable("constellations"){
            val constellationsViewModel: ConstellationsViewModel = viewModel()
            ConstellationsScreen(
                viewModel = constellationsViewModel,
                onGoBack = { navController.popBackStack() }
            )
        }
    }
}