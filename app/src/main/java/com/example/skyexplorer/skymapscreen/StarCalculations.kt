package com.example.skyexplorer.skymapscreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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
    val sptype: String
)

// --- 2. Komponent rysujący gwiazdy ---
@Composable
fun StarMap(starsJson: String) {
    // Dekodowanie danych JSON
    val stars = remember {
        Json.decodeFromString<List<Star>>(starsJson)
    }

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
