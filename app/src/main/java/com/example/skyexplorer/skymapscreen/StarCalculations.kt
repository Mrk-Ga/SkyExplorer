package com.example.skyexplorer.skymapscreen

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
    val OffsetSaver = run {
        androidx.compose.runtime.saveable.Saver<Offset, List<Float>>(
            save = { listOf(it.x, it.y) },
            restore = { Offset(it[0], it[1]) }
        )
    }
    var offset by rememberSaveable(stateSaver = OffsetSaver) {
        mutableStateOf(Offset.Zero)
    }
    var scale by rememberSaveable { mutableStateOf(1f) }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19)) // Nieco ładniejszy, głęboki granat
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    offset += pan
                    scale = (scale * zoom).coerceIn(0.5f, 5f)
                }
            }
    ) {

        val center = Offset(size.width / 2 + offset.x, size.height / 2 + offset.y)
        // Zmniejszyłem nieco promień, by był margines
        val radius = (size.minDimension / 2 * 0.95f) * scale

        // Okrąg horyzontu (clipping)
        val circularClip = Path().apply {
            addOval(Rect(center = center, radius = radius))
        }

        val positions = mutableMapOf<String, Offset>()

        // Rysujemy tło horyzontu (opcjonalnie)
        drawCircle(
            color = Color(0xFF111520),
            radius = radius,
            center = center
        )

        clipPath(circularClip) {
            stars.forEach { star ->
                val alt = star.alt ?: return@forEach
                val az = star.az ?: return@forEach // Azymut: 0=N, 90=E, 180=S, 270=W

                // Tylko gwiazdy nad horyzontem
                if (alt <= 0) return@forEach

                // --- KLUCZOWA POPRAWKA ---
                // Mapa nieba jest "widokiem od dołu". Jeśli Północ jest na górze (0°),
                // to Wschód (90°) musi być po LEWEJ stronie ekranu.
                // Standardowa trygonometria (sin) dla kąta 90° daje +1 (Prawo).
                // Dlatego musimy odwrócić azymut (360 - az).

                val adjustedAz = (360.0 - az) % 360.0

                val azRad = Math.toRadians(adjustedAz)
                val zenithAngle = Math.toRadians(90.0 - alt)

                // Projekcja Stereograficzna
                // r = R * tan(z / 2) -> zachowuje kąty, dobre dla konstelacji
                val r = radius * tan(zenithAngle / 2.0)

                // Pozycja (X=sin, Y=-cos bo 0 jest na górze)
                val x = center.x + (r * sin(azRad)).toFloat()
                val y = center.y - (r * cos(azRad)).toFloat()

                val pos = Offset(x, y)
                positions[star.name] = pos   // tylko gwiazdy widoczne!

                // =================================================================
                // NOWA SEKCJA OBLICZANIA KOLORU I ROZMIARU
                // =================================================================

                // 1. Kolor (bez zmian, ale dla porządku wklejam całość)
                val baseColor = when {
                    star.sptype.startsWith("O") -> Color(0xFF9BB0FF) // Niebieskie
                    star.sptype.startsWith("B") -> Color(0xFFAABFFF)
                    star.sptype.startsWith("A") -> Color(0xFFCAD7FF) // Biało-niebieskie
                    star.sptype.startsWith("F") -> Color(0xFFF8F7FF)
                    star.sptype.startsWith("G") -> Color(0xFFFFF4E8) // Żółte (jak Słońce)
                    star.sptype.startsWith("K") -> Color(0xFFFFE0B0) // Pomarańczowe
                    star.sptype.startsWith("M") -> Color(0xFFFFC8A0) // Czerwone
                    else -> Color.White
                }

                // 2. Obliczanie rozmiaru w zależności od magnitudy
                // Przyjmujemy granicę widoczności. Gwiazdy > 6.5 mag będą najmniejsze.
                val magnitudeLimit = 6.5

                // Odwracamy skalę: im mniejsza magnitudo (jaśniejsza gwiazda),
                // tym większy wynik 'brightnessScore'.
                // coerceAtLeast(0.2) zapewnia minimalny rozmiar dla bardzo słabych gwiazd.
                val brightnessScore = (magnitudeLimit - star.magnitude).coerceAtLeast(0.2)

                // Bazowy mnożnik wielkości.
                // Zwiększ wartość '2.0f', jeśli chcesz, aby WSZYSTKIE gwiazdy były większe.
                // Mnożymy przez 'scale', aby gwiazdy rosły wraz z zoomem mapy.
                val baseSizeFactor = 2.0f * scale

                // Wstępny promień
                val rawRadius = (baseSizeFactor * brightnessScore).toFloat()

                // Ostateczne ograniczenie promienia (sztywne ramy).
                // Min: 1.5px * scale (żeby zawsze było widać kropkę)
                // Max: 25px * scale (żeby Syriusz czy planety nie były wielkimi plamami)
                val starRadius = rawRadius.coerceIn(1.5f * scale, 25f * scale)


                // 3. Obliczanie alfy (przezroczystości)
                // Słabsze gwiazdy są nieco bardziej przezroczyste
                val intensity = (1.0 - (star.magnitude / magnitudeLimit))
                    .coerceIn(0.4, 1.0) // Min alpha 0.4, max 1.0
                    .toFloat()

                val finalColor = baseColor.copy(alpha = intensity)

                // RYSOWANIE

                // Poświata (większa i bardziej przejrzysta)
                // Rysujemy ją tylko dla jaśniejszych gwiazd (np. jaśniejszych niż 3 mag),
                // żeby nie robić "zupy" na ekranie przy dużej ilości słabych gwiazd.

                if (star.magnitude < 3.0) {
                    drawCircle(
                        color = finalColor.copy(alpha = 0.15f),
                        radius = starRadius * 1.8f, // Poświata 1.8x większa od jądra
                        center = pos
                    )
                }



                // Jądro gwiazdy
                drawCircle(
                    color = finalColor,
                    radius = starRadius,
                    center = pos
                )
            }

            // ---- RYSOWANIE LINI KONSTELACJI ----
            // (Odkomentuj i użyj jeśli masz dane w constellations)

            /*
            constellations.forEach { constellation ->
                constellation.segments.forEach { segment ->
                    val (star1, star2) = segment
                    val p1 = positions[star1]
                    val p2 = positions[star2]

                    if (p1 != null && p2 != null) {
                        drawLine(
                            color = Color.White.copy(alpha = 0.3f),
                            start = p1,
                            end = p2,
                            strokeWidth = 1.5f * scale
                        )
                    }
                }
            }

             */



        }



        // Ramka horyzontu
        drawCircle(
            color = Color(0xFF445577),
            radius = radius,
            center = center,
            style = Stroke(width = 4f)
        )

        // ---- KIERUNKI ŚWIATA (N, S, E, W) ----
        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#AAAAAA")
            textSize = 40f * scale.coerceIn(0.8f, 1.5f)
            textAlign = android.graphics.Paint.Align.CENTER
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }

        val directions = listOf(
            "N" to 0.0,
            "E" to 90.0,
            "S" to 180.0,
            "W" to 270.0
        )

        directions.forEach { (label, az) ->
            // Tutaj też stosujemy odbicie lustrzane dla etykiet!
            val adjustedAz = (360.0 - az) % 360.0
            val azRad = Math.toRadians(adjustedAz)

            // Tekst rysujemy nieco poza okręgiem
            val textR = radius + (30f * scale)

            val x = center.x + (textR * sin(azRad)).toFloat()
            val y = center.y - (textR * cos(azRad)).toFloat() + (textPaint.textSize / 3)

            drawContext.canvas.nativeCanvas.drawText(label, x, y, textPaint)
        }
    }
}
