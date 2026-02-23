package com.example.skyexplorer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.skyexplorer.R
import com.example.skyexplorer.ui.theme.FabNavBarButton
import com.example.skyexplorer.ui.theme.SkyExplorerTheme
import com.example.skyexplorer.ui.theme.StarBlue
import com.example.skyexplorer.ui.theme.StarDark
import com.example.skyexplorer.ui.theme.StarLightBlue

sealed class BottomNavItem(
    val label: String,
    val icon: ImageVector
) {
    object Camera : BottomNavItem("Camera", Icons.Filled.CameraAlt)
    object SkyMap : BottomNavItem("Sky map", Icons.Filled.Info)
    object Constellations : BottomNavItem("Info", Icons.Filled.Info)
}
@Composable
fun AppNavigationBar(
    onNavigateToCamera: () -> Unit,
    onNavigateToConstellations: () -> Unit,
    onNavigateToSkyMap: () -> Unit
) {
        Surface(
            //shape = RoundedCornerShape(40.dp),
            color = StarBlue, //MaterialTheme.colorScheme.surface
            tonalElevation = 8.dp,
            shadowElevation = 10.dp,
            contentColor = Color.White,
            modifier = Modifier.padding(23.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(40.dp),
                    ambientColor = Color(0xFF043D4F),
                    spotColor = Color(0xFF1D5770)
                )
                .background(Color.Transparent)

        ) {
            Row(
                modifier = Modifier
                    .height(72.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 45.dp)
                ,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateToCamera) {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        imageVector = Icons.Filled.CameraAlt,
                        contentDescription = null
                    )
                }

                //Spacer(Modifier.width(56.dp)) // miejsce na FAB

                FloatingActionButton(
                    onClick = onNavigateToSkyMap,
                    shape = CircleShape,
                    //modifier = Modifier.align(Alignment.CenterHorizontally),
                    containerColor = Color.Transparent,
                    elevation = FloatingActionButtonDefaults.elevation(20.dp)
                    ) {
                    Icon(
                        painter = painterResource(id = R.drawable.skyexplorer_nav_icon_clear),
                        contentDescription = "Navigate to Sky Map",
                        modifier = Modifier.size(70.dp).background(FabNavBarButton),
                        tint = Color.White
                    )
                }

                IconButton(onClick = onNavigateToConstellations) {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        imageVector = Icons.Filled.Info,
                        contentDescription = null)
                }


            }


        }
    }



@Preview
@Composable
fun AppNavigationBarPreview() {
    SkyExplorerTheme {
        AppNavigationBar(
            onNavigateToCamera = {},
            onNavigateToConstellations = {},
            onNavigateToSkyMap = {}
        )
    }
}
