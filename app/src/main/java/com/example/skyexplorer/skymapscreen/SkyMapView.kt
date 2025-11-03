package com.example.skyexplorer.skymapscreen

import android.graphics.Canvas
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun SkyMapView(
    stars: List<Star>,
    constellations: List<Constellation>
) {
    // Stan przesunięcia i skalowania
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var scale by remember { mutableStateOf(1f) }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    offsetX += pan.x
                    offsetY += pan.y
                    scale = (scale * zoom).coerceIn(0.5f, 3f) // ogranicz zoom
                }
            }
    ) {
        val centerX = size.width / 2
        val centerY = size.height / 2

        // Rysuj konstelacje (linie)
        constellations.forEach { constellation ->
            constellation.lines.forEach { (startId, endId) ->
                val s = stars.find { it.id == startId }
                val e = stars.find { it.id == endId }
                if (s != null && e != null) {
                    val x1 = (centerX + (s.az / 180.0 * centerX).toFloat()) * scale + offsetX
                    val y1 = (centerY - (s.alt / 90.0 * centerY).toFloat()) * scale + offsetY
                    val x2 = (centerX + (e.az / 180.0 * centerX).toFloat()) * scale + offsetX
                    val y2 = (centerY - (e.alt / 90.0 * centerY).toFloat()) * scale + offsetY

                    drawLine(
                        color = Color.Gray,
                        start = Offset(x1, y1),
                        end = Offset(x2, y2),
                        strokeWidth = 1f * scale
                    )
                }
            }
        }

        // Rysuj gwiazdy
        stars.forEach { star ->
            val x = (centerX + (star.az / 180.0 * centerX).toFloat()) * scale + offsetX
            val y = (centerY - (star.alt / 90.0 * centerY).toFloat()) * scale + offsetY

            val radius = (6f - star.magnitude).coerceAtLeast(1.0) * scale
            drawCircle(
                color = Color.White,
                radius = radius.toFloat(),
                center = Offset(x, y)
            )
        }
    }
}

