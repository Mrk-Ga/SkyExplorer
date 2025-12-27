package com.example.skyexplorer


import SkyMapViewModel
import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.skyexplorer.camera.CameraScreen
import com.example.skyexplorer.camera.CameraViewModel
import com.example.skyexplorer.camera.CameraViewModelFactory
import com.example.skyexplorer.camera.LocalRepository
import com.example.skyexplorer.constellations.ConstellationsScreen
import com.example.skyexplorer.constellations.ConstellationsViewModel
import com.example.skyexplorer.photoGallery.PhotoGalleryScreen
import com.example.skyexplorer.photoGallery.PhotoGalleryViewModel
import com.example.skyexplorer.data.*
import com.example.skyexplorer.skymapscreen.SkyMapRepository
import com.example.skyexplorer.skymapscreen.SkyMapScreen
import com.example.skyexplorer.skymapscreen.SkyMapViewModelFactory
import com.google.accompanist.navigation.animation.AnimatedNavHost

@OptIn(ExperimentalAnimationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("ViewModelConstructorInComposable")
@androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION])
@Composable
fun AppNavHost(navController: NavHostController, repository: SkyMapRepository) {
    NavHost(
        navController = navController,
        startDestination = "skymap",
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(animationDuration)
            ) + fadeIn()
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(animationDuration)
            ) + fadeOut()
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(animationDuration)
            ) + fadeIn()
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(animationDuration)
            ) + fadeOut()
        }
    ){


        composable("skymap")  {
            val skyMapViewModel: SkyMapViewModel = viewModel(
                                                    factory = SkyMapViewModelFactory(repository))
            SkyMapScreen(
                viewModel = skyMapViewModel,
                onNavigateToCamera = { navController.navigate("camera") },
                onNavigateToConstellations = { navController.navigate("assets/constellations") },
                onNavigateToSkyMap = { navController.navigate("skymap") }
            )
        }


        composable("camera") {
            val app = LocalContext.current.applicationContext as SkyExplorerApp
            val repo = app.cameraRepository
            val cameraViewModel: CameraViewModel = viewModel(
                factory = CameraViewModelFactory(repo, app)
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

                onNavigateToCamera = { navController.navigate("camera") },
                onNavigateToConstellations = { navController.navigate("assets/constellations") },
                onNavigateToSkyMap = { navController.navigate("skymap") },
                onNavigateToPhotoGallery = { item ->
                    navController.navigate("gallery/${item}")

                }
            )
        }

        composable(
            route = "gallery/{item}",
            arguments = listOf(
                navArgument("item") { type = NavType.StringType }
            )
        ) {
            val viewModel: PhotoGalleryViewModel = viewModel()
            PhotoGalleryScreen(
                viewModel = viewModel,
                constellationName = it.arguments?.getString("item") ?: "",
                onNavigateToConstellations = {navController.navigate("assets/constellations")
                }
            )

        }
    }
}