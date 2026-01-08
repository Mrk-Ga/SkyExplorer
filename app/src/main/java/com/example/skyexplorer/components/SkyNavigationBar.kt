package com.example.skyexplorer.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.skyexplorer.R
import com.example.skyexplorer.ui.theme.SkyExplorerTheme

@Composable
fun AppNavigationBar(
    onNavigateToCamera: () -> Unit,
    onNavigateToConstellations: () -> Unit,
    onNavigateToSkyMap: () -> Unit
){
    NavigationBar(windowInsets = NavigationBarDefaults.windowInsets,
        containerColor = Color.Transparent,
        contentColor = Color.White
    ) {

        NavigationBarItem(
            selected = false,
            onClick = {
                onNavigateToCamera()
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
            }
        )
        NavigationBarItem(
            selected = false,
            onClick = {
                onNavigateToSkyMap()
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.skyexplorer_nav_icon_clear),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = Color.Unspecified

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
                    modifier = Modifier.size(40.dp)
                )
            }
        )

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
