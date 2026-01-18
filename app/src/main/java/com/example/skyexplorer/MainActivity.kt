package com.example.skyexplorer

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.skyexplorer.skymapscreen.SkyMapRepositoryImpl
import com.example.skyexplorer.ui.theme.SkyExplorerTheme

//import com.example.navigationdemo.ui.theme.NavigationDemoTheme

class MainActivity : ComponentActivity() {
    @androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION])
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

            setContent {
                SkyExplorerTheme {
                    //GradientBackground {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        )  {
                            //AppNavigation()
                            try {
                                val list = assets.list("")?.toList()
                                Log.d("ASSETS", "Zawartość assets: $list")
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            val navController = rememberNavController()
                            val repository = SkyMapRepositoryImpl(application)
                            AppNavHost(navController, repository)
                        }
                    //}

            }
        }
    }
}