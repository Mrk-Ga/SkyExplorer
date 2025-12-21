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
import kotlin.math.*

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

/**
 * Prosty wektor 3D (bez zależności od OpenGL).
 */
private data class Vec3(val x: Double, val y: Double, val z: Double)

/**
 * Konwersja (az, alt) -> wektor jednostkowy na sferze.
 *
 * Założenia osi (układ świata):
 * - +Y = "góra" (zenit)
 * - +Z = "północ" (przód, gdy az=0)
 * - +X = "wschód" (prawo, gdy az=90)
 *
 * az w stopniach: 0=N, 90=E, 180=S, 270=W
 * alt w stopniach: -90..+90
 */
private fun altAzToUnitVector(azDeg: Double, altDeg: Double): Vec3 {
    val az = Math.toRadians(azDeg)
    val alt = Math.toRadians(altDeg)

    val cosAlt = cos(alt)
    val x = cosAlt * sin(az)
    val y = sin(alt)
    val z = cosAlt * cos(az)
    return Vec3(x, y, z)
}

/**
 * Obrót wektora wokół osi Y (yaw) — obrót w poziomie (azymut).
 */
private fun rotateY(v: Vec3, degrees: Double): Vec3 {
    val a = Math.toRadians(degrees)
    val ca = cos(a)
    val sa = sin(a)
    // [ ca 0 sa ] [x]
    // [  0 1  0 ] [y]
    // [-sa 0 ca ] [z]
    return Vec3(
        x = v.x * ca + v.z * sa,
        y = v.y,
        z = -v.x * sa + v.z * ca
    )
}

/**
 * Obrót wektora wokół osi X (pitch) — patrzenie w górę/dół.
 * Dodatni pitch: "w dół" (zgodnie z typową konwencją kamery), dlatego zwykle podajemy -alt.
 */
private fun rotateX(v: Vec3, degrees: Double): Vec3 {
    val a = Math.toRadians(degrees)
    val ca = cos(a)
    val sa = sin(a)
    // [1  0   0] [x]
    // [0 ca -sa] [y]
    // [0 sa  ca] [z]
    return Vec3(
        x = v.x,
        y = v.y * ca - v.z * sa,
        z = v.y * sa + v.z * ca
    )
}

/**
 * Świat -> Kamera: ustawiamy kamerę tak, aby patrzyła w kierunku (centerAz, centerAlt).
 *
 * Robimy to jako obrót odwrotny:
 * - najpierw "odejmujemy" azymut: yaw = -centerAz
 * - potem "odejmujemy" wysokość: pitch = +centerAlt (zależnie od przyjętej osi)
 *
 * Jeśli obraz będzie "odwrócony w pionie" lub "w lewo/prawo", najczęściej wystarczy zmienić znak
 * w jednym z tych obrotów (patrz komentarze przy yaw/pitch poniżej).
 */
private fun worldToCamera(vWorld: Vec3, centerAzDeg: Double, centerAltDeg: Double): Vec3 {
    // 1. Azymut (lewo/prawo) - zazwyczaj minus jest poprawny dla kamery
    val v1 = rotateY(vWorld, -centerAzDeg)

    // 2. Wysokość (góra/dół)
    // ZMIANA: Dodajemy minus przed centerAltDeg.
    // Jeśli ruszasz telefonem w górę, a gwiazdy uciekają też w górę (zamiast chować się pod dolną krawędź),
    // to zmiana znaku tutaj to naprawi.
    val v2 = rotateX(v1, -centerAltDeg)

    return v2
}
private fun projectPerspective(
    vCam: Vec3,
    centerX: Float,
    centerY: Float,
    halfW: Float,
    halfH: Float,
    f: Double,
    scale: Float,
    offset: Offset
): Offset? {
    // Jeśli punkt jest za kamerą (z < 0), nie rysujemy go
    if (vCam.z <= 0.1) return null

    val nx = (vCam.x / vCam.z) * f
    val ny = (vCam.y / vCam.z) * f

    val x = centerX + (nx * halfW).toFloat() * scale + offset.x

    // ZMIANA: centerY - ... zamiast centerY + ...
    // Dzięki temu dodatnie 'ny' (gwiazda w górze) da mniejszą wartość Y na ekranie (bliżej górnej krawędzi).
    val y = centerY - (ny * halfH).toFloat() * scale + offset.y

    return Offset(x, y)
}

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
        onDispose { viewModel.stopSensors() }
    }

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

    ) {
        val centerAz = viewModel.viewDirection.azimuth
        val centerAlt = viewModel.viewDirection.altitude

        // FOV w poziomie (przyjmujemy jako bazę). Pion też będzie wyglądał OK przez halfH.
        val fovDeg = viewModel.fieldOfView.coerceIn(10.0, 160.0)
        val f = 1.0 / tan(Math.toRadians(fovDeg) / 2.0)

        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val halfW = size.width / 2f
        val halfH = size.height / 2f

        // pozycje tylko dla widocznych (przed kamerą + po projekcji)
        val positions = mutableMapOf<Int, Offset>()

        // ---- RYSOWANIE GWIAZD (3D -> perspektywa) ----
        stars.forEach { star ->
            val alt = star.alt ?: return@forEach
            val az = star.az ?: return@forEach

            // 1) gwiazda jako wektor 3D w świecie
            val vWorld = altAzToUnitVector(az, alt)

            // 2) obrót świata do układu kamery (telefonu)
            val vCam = worldToCamera(vWorld, centerAz, centerAlt)

            // 3) projekcja perspektywiczna na ekran
            val pos = projectPerspective(
                vCam = vCam,
                centerX = centerX,
                centerY = centerY,
                halfW = halfW,
                halfH = halfH,
                f = f,
                scale = scale,
                offset = offset
            ) ?: return@forEach

            positions[star.id] = pos

            // ---- KOLOR / ROZMIAR (jak u Ciebie) ----
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

        // ---- RYSOWANIE KONSTELACJI (bez zmian logiki) ----
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
