package com.example.skyexplorer.skymapscreen

import SkyMapViewModel
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import angularDistance
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.cos

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
    val segments: List<List<Int>>
)

// ---- RYSOWANIE MAPY NIEBA ----

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StarMap(
    stars: List<Star>,
    viewModel: SkyMapViewModel,
    constellations: List<Constellation>
) {

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.startSensors(context)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopSensors()
        }
    }

    // --- Diagnostyka ---
    LaunchedEffect(stars.size, constellations.size) {
        Log.d("STAR_MAP_DEBUG", "Liczba gwiazd: ${stars.size}")
        Log.d("STAR_MAP_DEBUG", "Liczba konstelacji: ${constellations.size}")
    }

    var scale by rememberSaveable { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }


    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19))
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    offset += pan
                    scale = (scale * zoom).coerceIn(0.5f, 5f)
                }
            }
    ) {

        val centerAz = viewModel.viewDirection.azimuth
        val centerAlt = viewModel.viewDirection.altitude
        val halfFov = viewModel.fieldOfView / 2

        // 📌 Telefon skierowany pod horyzont → nic nie rysujemy
        //if (centerAlt <= 0.0) return@Canvas

        val centerX = size.width / 2 + offset.x
        val centerY = size.height / 2 + offset.y
        val halfWidth = size.width / 2
        val halfHeight = size.height / 2

        val positions = mutableMapOf<Int, Offset>()

        // ---- RYSOWANIE GWIAZD ----

        stars.forEach { star ->
            val alt = star.alt ?: return@forEach
            val az = star.az ?: return@forEach

            //if (alt < 0) return@forEach

            val dist = angularDistance(
                az, alt,
                centerAz, centerAlt
            )

            if (dist > halfFov) return@forEach

            val dAz = ((az - centerAz + 540) % 360) - 180
            val dAlt = alt - centerAlt

            val x = centerX + (dAz / halfFov) * halfWidth * scale
            val y = centerY - (dAlt / halfFov) * halfHeight * scale

            val pos = Offset(x.toFloat(), y.toFloat())
            positions[star.id] = pos

            // ---- KOLOR GWIAZDY ----
            val baseColor = when {
                star.sptype.startsWith("O") -> Color(0xFF9BB0FF)
                star.sptype.startsWith("B") -> Color(0xFFAABFFF)
                star.sptype.startsWith("A") -> Color(0xFFCAD7FF)
                star.sptype.startsWith("F") -> Color(0xFFF8F7FF)
                star.sptype.startsWith("G") -> Color(0xFFFFF4E8)
                star.sptype.startsWith("K") -> Color(0xFFFFE0B0)
                star.sptype.startsWith("M") -> Color(0xFFFFC8A0)
                else -> Color.White
            }

            val magnitudeLimit = 6.5
            val brightnessScore = (magnitudeLimit - star.magnitude).coerceAtLeast(0.2)
            val baseSizeFactor = 2.0f * scale
            val rawRadius = (baseSizeFactor * brightnessScore).toFloat()
            val starRadius = rawRadius.coerceIn(1.5f * scale, 25f * scale)

            val intensity = (1.0 - (star.magnitude / magnitudeLimit))
                .coerceIn(0.4, 1.0)
                .toFloat()

            val finalColor = baseColor.copy(alpha = intensity)

            if (star.magnitude < 3.0) {
                drawCircle(
                    color = finalColor.copy(alpha = 0.15f),
                    radius = starRadius * 1.8f,
                    center = pos
                )
            }

            drawCircle(
                color = finalColor,
                radius = starRadius,
                center = pos
            )
        }

        // ---- RYSOWANIE KONSTELACJI ----

        val constellationColor = Color.White.copy(alpha = 0.35f)

        constellations.forEach { constellation ->
            val path = Path()
            var isPathEmpty = true

            val allStarsVisible = constellation.segments
                .flatten()
                .distinct()
                .all { positions.containsKey(it) }

            if (!allStarsVisible) return@forEach

            constellation.segments.forEach { segment ->
                if (segment.size >= 2) {
                    val posA = positions[segment[0]]
                    val posB = positions[segment[1]]

                    if (posA != null && posB != null) {
                        path.moveTo(posA.x, posA.y)
                        path.lineTo(posB.x, posB.y)
                        isPathEmpty = false
                    }
                }
            }

            if (!isPathEmpty) {
                drawPath(
                    path = path,
                    color = constellationColor,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    }
}
