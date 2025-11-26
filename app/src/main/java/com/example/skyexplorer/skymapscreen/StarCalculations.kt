package com.example.skyexplorer.skymapscreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

// --- 1. Model danych ---
@kotlinx.serialization.Serializable
data class Star(
    val id: Int,
    val name: String,
    val ra: Double,     // Prawo wzniesienia (w godzinach lub stopniach)
    val dec: Double,    // Deklinacja (stopnie)
    val magnitude: Double,
    val sptype: String,
    var alt: Double?=null,
    var az: Double?=null
)


@Composable
fun StarMap(stars: List<Star>) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    var scale by remember { mutableStateOf(1f) }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    offset += pan
                    scale = (scale * zoom).coerceIn(0.5f, 3f)
                }
            }
    ) {
        val center = Offset(size.width / 2 + offset.x, size.height / 2 + offset.y)
        val radius = size.minDimension / 2 * scale

        // Tworzymy maskę w kształcie okręgu (horyzont)
        val circularClip = Path().apply {
            addOval(Rect(center = center, radius = radius))
        }

        clipPath(circularClip) {
            stars.forEach { star ->
                val alt = star.alt ?: return@forEach
                val az = star.az ?: return@forEach

                // Jeśli gwiazda jest pod horyzontem – pomiń
                if (alt <= 0) return@forEach

                // Konwersja Alt/Az → współrzędne na ekranie (projekcja zenitalna)
                val altRad = Math.toRadians(alt)
                val azRad = Math.toRadians(az)

                val r = (radius * (90.0 - alt) / 90.0).toFloat()
                val x = center.x + (r * sin(azRad)).toFloat()
                val y = center.y - (r * cos(azRad)).toFloat()

                //val brightnessFactor = 10.0.pow(-star.magnitude / 2.5) // logarytmiczna jasność
                //val starRadius = (brightnessFactor * 20f).toFloat().coerceIn(1f, 12f)

                val color = when {
                    star.sptype.startsWith("O") -> Color(0xFF9BB0FF)
                    star.sptype.startsWith("B") -> Color(0xFFAABFFF)
                    star.sptype.startsWith("A") -> Color(0xFFCAD7FF)
                    star.sptype.startsWith("F") -> Color(0xFFF8F7FF)
                    star.sptype.startsWith("G") -> Color(0xFFFFF4E8)
                    star.sptype.startsWith("K") -> Color(0xFFFFE0B0)
                    star.sptype.startsWith("M") -> Color(0xFFFFC8A0)
                    else -> Color.White
                }

                val magnitude = star.magnitude

// Logarytmiczna jasność (rzeczywista fizyczna)
                val brightnessFactor = 10.0.pow(-magnitude / 2.5)

// Rozmiar gwiazdy na ekranie
                val starRadius = (brightnessFactor * 20f).toFloat().coerceIn(1f, 12f)

// Intensywność koloru
                val intensity = (1.0 - magnitude / 8.0).coerceIn(0.3, 1.0).toFloat()
                val finalColor = color.copy(alpha = intensity)

// Glow – lekka poświata
                drawCircle(
                    color = finalColor.copy(alpha = 0.15f),
                    radius = starRadius * 1f,
                    center = Offset(x, y)
                )

// Właściwa gwiazda
                drawCircle(
                    color = finalColor,
                    radius = starRadius,
                    center = Offset(x, y)
                )
            }
        }

        // Obrys horyzontu
        drawCircle(
            color = Color.Gray,
            radius = radius,
            center = center,
            style = Stroke(width = 2f)
        )

        // Kierunki świata (N, E, S, W)
        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 34f
            textAlign = android.graphics.Paint.Align.CENTER
        }

        listOf(
            "N" to 0.0,
            "E" to 90.0,
            "S" to 180.0,
            "W" to 270.0
        ).forEach { (label, az) ->
            val azRad = Math.toRadians(az)
            val x = center.x + (radius * sin(azRad)).toFloat()
            val y = center.y - (radius * cos(azRad)).toFloat()
            drawContext.canvas.nativeCanvas.drawText(label, x, y, textPaint)
        }
    }
}



