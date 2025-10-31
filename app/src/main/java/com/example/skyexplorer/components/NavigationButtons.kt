package com.example.skyexplorer.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.Info
import com.example.skyexplorer.skymapscreen.SkyMapIntent

@Composable
fun ForwardButton(onClick: () -> Unit){
    OutlinedIconButton(
        onClick = {onClick()},
        modifier = Modifier.padding(20.dp).size(60.dp)

    )
    {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Go to next screen" ,
            modifier = Modifier.size(40.dp)
        )
    }
}

@Composable
fun BackwardButton(onClick: () -> Unit){
    OutlinedIconButton(
        onClick = {onClick()},
        modifier = Modifier.padding(20.dp).size(60.dp)

    )
    {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Go to previous screen",
            modifier = Modifier.size(40.dp)
        )
    }
}

@Composable
fun CameraButton(onClick: () -> Unit){
    OutlinedIconButton(
        onClick = {onClick()},
        modifier = Modifier.padding(20.dp).size(60.dp)

    )
    {
        Icon(
            imageVector = Icons.Filled.CameraAlt,
            contentDescription = "Go to camera",
            modifier = Modifier.size(40.dp)
        )
    }
}

@Composable
fun InfoButton(onClick: () -> Unit){
    OutlinedIconButton(
        onClick = {onClick()},
        modifier = Modifier.padding(20.dp).size(60.dp)

    )
    {
        Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = "Go to informations",
            modifier = Modifier.size(40.dp)
        )
    }
}

