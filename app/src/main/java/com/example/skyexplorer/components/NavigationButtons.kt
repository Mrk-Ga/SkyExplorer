package com.example.skyexplorer.components

import android.R.attr.scaleX
import android.R.attr.scaleY
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp

@Composable
fun ForwardButton(onClick: () -> Unit){
    OutlinedIconButton(
        onClick = {onClick()},
        modifier = Modifier.padding(20.dp).size(60.dp),
        border = ButtonDefaults.outlinedButtonBorder(),
        colors = IconButtonColors(
            containerColor = Color.Transparent,
            contentColor = Color.Cyan,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.Transparent
        )


    )
    {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Go to next screen" ,
            modifier = Modifier.size(40.dp),
            tint = Color.Cyan

        )
    }
}

@Composable
fun BouncyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    content: @Composable () -> Unit
) {
    val scale = remember { mutableStateOf(1f) }

    val animatedScale by animateFloatAsState(
        targetValue = scale.value,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ), label = ""
    )

    Box(

        modifier = Modifier
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        // Na wciśnięcie
                        scale.value = 0.85f
                        val released = try { awaitRelease() } catch (e: Exception) { false }

                        // Po puszczeniu
                        scale.value = 1f
                    },
                    onTap = { onClick() }
                )
            }
                //.padding(15.dp)
                //.size(80.dp)

    ) {
        content()


    }
}


