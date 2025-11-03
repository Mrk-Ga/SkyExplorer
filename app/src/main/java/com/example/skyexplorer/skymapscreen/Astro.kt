package com.example.skyexplorer.skymapscreen


import android.os.Build
import androidx.annotation.RequiresApi
import java.time.ZonedDateTime
import kotlin.math.*

/** Użyteczne narzędzia */
private fun normalizeDegrees(x: Double): Double {
    var v = x % 360.0
    if (v < 0) v += 360.0
    return v
}

private fun toRadians(d: Double) = Math.toRadians(d)
private fun toDegrees(r: Double) = Math.toDegrees(r)

/** Julian Date (UTC) — algorytm wg Meeusa (wystarczająco dokładny do mapy nieba) */
@RequiresApi(Build.VERSION_CODES.O)
fun julianDate(t: ZonedDateTime): Double {
    val year = t.year
    var month = t.monthValue
    val day = t.dayOfMonth +
            (t.hour / 24.0) +
            (t.minute / (24.0 * 60.0)) +
            (t.second / (24.0 * 3600.0)) +
            (t.nano / (24.0 * 3600.0 * 1e9))

    var y = year
    if (month <= 2) { y -= 1; month += 12 }

    val a = y / 100
    val b = 2 - a + (a / 4)
    val jd = floor(365.25 * (y + 4716)) +
            floor(30.6001 * (month + 1)) +
            day + b - 1524.5
    return jd
}

/** Greenwich Mean Sidereal Time (deg). Przybliżenie dobre do wizualizacji. */
fun gmstDegrees(jd: Double): Double {
    val T = (jd - 2451545.0) / 36525.0
    // GMST w sekundach kąta (IAU 1982, uproszcz.)
    var gmst = 280.46061837 +
            360.98564736629 * (jd - 2451545.0) +
            0.000387933 * T * T -
            (T * T * T) / 38710000.0
    return normalizeDegrees(gmst)
}

/** Local Sidereal Time w stopniach: LST = GMST + długość geogr. (E+) */
fun lstDegrees(jd: Double, longitudeDeg: Double): Double {
    return normalizeDegrees(gmstDegrees(jd) + longitudeDeg)
}

/** RA/Dec (deg) → Alt/Az (deg) dla obserwatora na lat/lon i danym czasie (UTC) */
@RequiresApi(Build.VERSION_CODES.O)
fun raDecToAltAzDeg(
    raDeg: Double,
    decDeg: Double,
    latDeg: Double,
    lonDeg: Double,
    tUtc: ZonedDateTime
): Pair<Double, Double> {
    val jd = julianDate(tUtc)
    val lst = lstDegrees(jd, lonDeg) // [deg]

    // Hour Angle [deg]
    val ha = normalizeDegrees(lst - raDeg)

    val haRad = toRadians(ha)
    val decRad = toRadians(decDeg)
    val latRad = toRadians(latDeg)

    val sinAlt = sin(decRad) * sin(latRad) + cos(decRad) * cos(latRad) * cos(haRad)
    val alt = asin(sinAlt)

    // Uwaga: azymut liczony astronomicznie od południka? Poniżej liczymy 0°=N, 90°=E
    val cosAz = (sin(decRad) - sin(alt) * sin(latRad)) / (cos(alt) * cos(latRad))
    // Ochrona przed błędami numerycznymi
    val cosAzClamped = cosAz.coerceIn(-1.0, 1.0)
    var az = acos(cosAzClamped)

    // rozstrzygnięcie ćwiartek wg znaku sin(HA)
    if (sin(haRad) > 0) {
        az = 2 * Math.PI - az
    }

    return Pair(toDegrees(alt), toDegrees(az))
}

/** Prosta korekcja refrakcji (opcjonalnie). Zwraca skorygowaną wysokość w deg. */
fun applyAtmosphericRefraction(altDeg: Double): Double {
    // Kąt wzniesienia w stopniach, prosta formuła Bennetta (przybliżenie)
    if (altDeg <= -1.0) return altDeg
    val altRad = toRadians(altDeg)
    val R = 1.02 / tan(altRad + toRadians(10.3 / (altDeg + 5.11))) / 60.0 // w stopniach
    return altDeg + R
}