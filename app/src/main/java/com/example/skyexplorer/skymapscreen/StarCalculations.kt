package com.example.skyexplorer.skymapscreen

import android.util.Log
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.tan

// ---- MODELE ----

@kotlinx.serialization.Serializable
data class Star(
    val id: Int,
    val name: String,
    val ra: Double,
    val dec: Double,
    val magnitude: Double,
    val sptype: String,
    var alt: Double? = null,
    var az: Double? = null
)

@kotlinx.serialization.Serializable
data class Constellation(
    val id: String,
    val name: String,
    //val stars: List<String>,
    val segments: List<List<String>>
)


// ---- RYSOWANIE MAPY NIEBA ----

@Composable
fun StarMap(
    stars: List<Star>,
    constellations: List<Constellation>
) {
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

        // okrąg horyzontu
        val circularClip = Path().apply {
            addOval(Rect(center = center, radius = radius))
        }

        // pozycje tylko gwiazd NAD horyzontem
        val positions = mutableMapOf<String, Offset>()

        // ---- Rysowanie GWIAZD ----

        clipPath(circularClip) {
            stars.forEach { star ->

                val alt = star.alt ?: return@forEach
                val az = star.az ?: return@forEach

                // GWIAZDA MUSI BYĆ NAD HORYZONTEM
                if (alt <= 0) return@forEach

                val altRad = Math.toRadians(alt)
                val azRad = Math.toRadians(az)

                // STEREOGRAFICZNA projekcja
                val zenithAngle = 90 - alt
                var r = radius * 2 * tan(Math.toRadians(zenithAngle / 2))

// OGRANICZENIE — nic nie może wyjść poza horyzont
                r = r.coerceAtMost(radius.toDouble())

                val x = center.x - (r * sin(azRad)).toFloat()
                val y = center.y - (r * cos(azRad)).toFloat()


                val pos = Offset(x, y)
                positions[star.name] = pos   // tylko gwiazdy widoczne!

                // kolor
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

                // jasność & rozmiar
                val brightnessFactor = 10.0.pow(-star.magnitude / 2.5)
                val starRadius = (brightnessFactor * 20f)
                    .toFloat()
                    .coerceIn(1f, 12f)

                val intensity = (1.0 - star.magnitude / 8.0)
                    .coerceIn(0.3, 1.0)
                    .toFloat()

                val finalColor = color.copy(alpha = intensity)

                // poświata
                drawCircle(
                    color = finalColor.copy(alpha = 0.15f),
                    radius = starRadius * 1.5f,
                    center = pos
                )

                drawCircle(
                    color = finalColor,
                    radius = starRadius,
                    center = pos
                )
            }
        }

        // ---- RYSOWANIE KONSTELACJI ----
        // tylko jeśli OBA końce segmentu są nad horyzontem

/*
        constellations.forEach { constellation ->
            constellation.segments.forEach { segment ->

                val (aName, bName) = segment

                val a = positions[aName]   // null jeśli alt<=0
                val b = positions[bName]

                val aStar = stars.find { it.name == aName }
                val bStar = stars.find { it.name == bName }

                Log.e("ALT_TEST", "${aName}: alt=${aStar?.alt} az=${aStar?.az}")
                Log.e("ALT_TEST", "${bName}: alt=${bStar?.alt} az=${bStar?.az}")

                Log.d("CONST", "Checking $aName -> $bName | a=$a b=$b")

                if (a != null && b != null) {
                    drawLine(
                        color = Color.Cyan,
                        start = a,
                        end = b,
                        strokeWidth = 2.dp.toPx()
                    )
                }
            }
        }

 */

        // OKRĄG HORYZONTU
        drawCircle(
            color = Color.Gray,
            radius = radius,
            center = center,
            style = Stroke(width = 2f)
        )

        // Kierunki świata
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
