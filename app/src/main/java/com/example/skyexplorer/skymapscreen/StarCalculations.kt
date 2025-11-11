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
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.math.pow

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
/*
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
        // Centrum mapy (zenit)
        val center = Offset(size.width / 2 + offset.x, size.height / 2 + offset.y)
        val radius = size.minDimension / 2 * scale

        // Ścieżka okręgu - horyzont
        val circularClip = Path().apply {
            addOval(Rect(center = center, radius = radius))
        }

        // Przytnij rysowanie do kształtu koła
        clipPath(circularClip) {
            stars.forEach { star ->
                val alt = star.alt ?: return@forEach
                val az = star.az ?: return@forEach

                // Rzutowanie Alt/Az na ekran (projekcja zenitalna)
                val altClamped = alt.coerceIn(0.0, 90.0)
                val altRad = Math.toRadians(altClamped)
                val azRad = Math.toRadians(az)

                // Odległość od środka: zenit = środek, horyzont = krawędź
                val r = (radius * (90.0 - altClamped) / 90.0).toFloat()

                val x = center.x + (r * sin(azRad)).toFloat()
                val y = center.y - (r * cos(azRad)).toFloat()

                // Jasność: im mniejsza magnituda, tym większe kółko
                val brightness = (6.5 - star.magnitude).coerceIn(1.0, 5.0).toFloat()

                // Kolor wg typu widmowego
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

                drawCircle(
                    color = color.copy(alpha = 0.95f),
                    radius = brightness,
                    center = Offset(x, y)
                )
            }
        }

        // Ramka (horyzont)
        drawCircle(
            color = Color.Gray,
            radius = radius,
            center = center,
            style = Stroke(width = 2f)
        )

        // Kierunki świata na krawędzi
        val directions = listOf(
            "N" to 0.0,
            "E" to 90.0,
            "S" to 180.0,
            "W" to 270.0
        )
        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 32f
            textAlign = android.graphics.Paint.Align.CENTER
        }

        directions.forEach { (label, az) ->
            val azRad = Math.toRadians(az)
            val x = center.x + (radius * sin(azRad)).toFloat()
            val y = center.y - (radius * cos(azRad)).toFloat()
            drawContext.canvas.nativeCanvas.drawText(label, x, y, textPaint)
        }
    }
}



 */

// --- 2. Komponent rysujący gwiazdy ---
@Composable
fun StarMap(stars: List<Star>) {
    // Dekodowanie danych JSON
    /*
    val stars = remember {
        Json.decodeFromString<List<Star>>(starsJson)
    }

     */

    // Stan przesunięcia widoku
    var offset by remember { mutableStateOf(Offset.Zero) }
    var scale by remember { mutableStateOf(1f) }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    offset += pan
                    scale *= zoom
                }
            }
    ) {
        val centerX = size.width / 2 + offset.x
        val centerY = size.height / 2 + offset.y

        // Przybliżone odwzorowanie RA/DEC -> ekran
        val scaleFactor = 15f * scale // Im większe, tym "większy zoom"

        stars.forEach { star ->
            // RA i DEC przelicz na współrzędne ekranu
            val x = centerX + (star.ra * scaleFactor).toFloat()
            val y = centerY - (star.dec * scaleFactor).toFloat()

            // Jasność: im jaśniejsza gwiazda (mniejsza magnitude), tym większy promień
            val radius = (5.5 - star.magnitude).coerceIn(1.5, 5.0).toFloat()

            // Kolor wg typu widmowego
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

            drawCircle(
                color = color,
                radius = radius,
                center = Offset(x, y)
            )
        }
    }
}



