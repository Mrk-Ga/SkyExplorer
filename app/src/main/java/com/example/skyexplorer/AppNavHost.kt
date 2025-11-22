package com.example.skyexplorer


import SkyMapViewModel
import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.skyexplorer.camera.CameraScreen
import com.example.skyexplorer.camera.CameraViewModel
import com.example.skyexplorer.camera.LocalRepository
import com.example.skyexplorer.constellations.ConstellationsScreen
import com.example.skyexplorer.constellations.ConstellationsViewModel
import com.example.skyexplorer.photoGallery.PhotoGalleryScreen
import com.example.skyexplorer.photoGallery.PhotoGalleryViewModel
import com.example.skyexplorer.skymapscreen.*

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("ViewModelConstructorInComposable")
@androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION])
@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "skymap") {



        composable("skymap")  {
            val skyMapViewModel: SkyMapViewModel = viewModel()
            SkyMapScreen(
                viewModel = skyMapViewModel,
                onNavigateToCamera = { navController.navigate("camera") },
                onNavigateToConstellations = { navController.navigate("assets/constellations") }
            )
        }


        composable("camera") {
            val app = LocalContext.current.applicationContext as SkyExplorerApp
            val repo = app.cameraRepository
            val cameraViewModel: CameraViewModel = viewModel(
                factory = CameraViewModelFactory(repo)
            )
            CameraScreen(
                viewModel = cameraViewModel,
                onGoBack = { navController.popBackStack() }
            )
        }

        composable("assets/constellations"){
            val constellationsViewModel: ConstellationsViewModel = viewModel()
            ConstellationsScreen(
                viewModel = constellationsViewModel,
                onGoBack = { navController.popBackStack() },
                onGoToPhotoGallery = { navController.navigate("gallery") },
                onNavigateToCamera = {navController.navigate("camera")}
            )
        }

        composable("gallery") {
            val viewModel: PhotoGalleryViewModel = viewModel()
            PhotoGalleryScreen(
                viewModel = viewModel
            )

        }
    }
}