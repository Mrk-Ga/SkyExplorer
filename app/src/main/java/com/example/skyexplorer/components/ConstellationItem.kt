package com.example.skyexplorer.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ConstellationItem(){
    ListItem(
        headlineContent = { Text("Constellation") },
        supportingContent = { Icon(Icons.Filled.Favorite, contentDescription = "Localized description") })

}