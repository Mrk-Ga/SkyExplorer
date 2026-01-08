package com.example.skyexplorer


import android.annotation.SuppressLint
import android.os.Build
import com.example.skyexplorer.skymapscreen.SkyMapViewModel
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.skyexplorer.camera.CameraScreen
import com.example.skyexplorer.camera.CameraViewModel
import com.example.skyexplorer.camera.CameraViewModelFactory
import com.example.skyexplorer.constellations.ConstellationsScreen
import com.example.skyexplorer.constellations.ConstellationsViewModel
import com.example.skyexplorer.photoGallery.PhotoGalleryScreen
import com.example.skyexplorer.photoGallery.PhotoGalleryViewModel
import com.example.skyexplorer.data.*
import com.example.skyexplorer.skymapscreen.SkyMapRepository
import com.example.skyexplorer.skymapscreen.SkyMapScreen
import com.example.skyexplorer.skymapscreen.SkyMapViewModelFactory

@OptIn(ExperimentalAnimationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("ViewModelConstructorInComposable")
@androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION])
@Composable
fun AppNavHost(navController: NavHostController, repository: SkyMapRepository) {

    val skyMapViewModel: SkyMapViewModel = viewModel(
        factory = SkyMapViewModelFactory(repository))

    NavHost(
        navController = navController,
        startDestination = skyMapScreen,
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


        composable(skyMapScreen)  {

            SkyMapScreen(
                viewModel = skyMapViewModel,
                onNavigateToCamera = { navController.navigate(cameraScreen) },
                onNavigateToConstellations = { navController.navigate(constellationsScreen) },
                onNavigateToSkyMap = { navController.navigate(skyMapScreen) }
            )
        }


        composable(cameraScreen) {
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

        composable(constellationsScreen){
            val constellationsViewModel: ConstellationsViewModel = viewModel()
            ConstellationsScreen(
                viewModel = constellationsViewModel,

                onNavigateToCamera = { navController.navigate(cameraScreen) },
                onNavigateToConstellations = { navController.navigate(constellationsScreen) },
                onNavigateToSkyMap = { navController.navigate(skyMapScreen) },
                onNavigateToPhotoGallery = { item ->
                    navController.navigate("gallery/${item}")

                }
            )
        }

        composable(
            route = photoGalleryScreen,
            arguments = listOf(
                navArgument("item") { type = NavType.StringType }
            )
        ) {
            val viewModel: PhotoGalleryViewModel = viewModel()
            PhotoGalleryScreen(
                viewModel = viewModel,
                constellationName = it.arguments?.getString("item") ?: "",
                onNavigateToConstellations = {navController.navigate(constellationsScreen)},

            )

        }
    }
}