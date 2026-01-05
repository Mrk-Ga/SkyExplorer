package com.example.skyexplorer.skymapscreen

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.math.*
data class HorizontalCoordinates(val alt: Double, val az: Double)

@RequiresApi(Build.VERSION_CODES.O)
fun raDecToAltAz(
    raDeg: Double,
    decDeg: Double,
    latDeg: Double,
    lonDeg: Double,
    dateTime: ZonedDateTime
): HorizontalCoordinates {

    val ra = Math.toRadians(raDeg)
    val dec = Math.toRadians(decDeg)
    val lat = Math.toRadians(latDeg)

    // Obliczamy czas gwiazdowy (LST) w radianach
    val lst = localSiderealTime(dateTime, lonDeg)

    // Kąt godzinny (Hour Angle)
    var ha = lst - ra

    // Normalizacja HA do przedziału (-PI, PI) lub (0, 2PI)
    ha = (ha + 2 * Math.PI) % (2 * Math.PI)

    // 1. Obliczenie Wysokości (Altitude)
    val sinAlt = sin(dec) * sin(lat) + cos(dec) * cos(lat) * cos(ha)
    val alt = asin(sinAlt)

    // 2. Obliczenie Azymutu (Azimuth)
    // Wzór klasyczny daje azymut liczony od Południa (0=S, 90=W...)
    val y = sin(ha)
    val x = cos(ha) * sin(lat) - tan(dec) * cos(lat)

    val azSouth = atan2(y, x)

    // Konwersja na azymut nawigacyjny (0=N, 90=E, 180=S, 270=W)
    // Dodajemy PI (180 stopni), aby przenieść 0 z S na N
    var azNorth = azSouth + Math.PI

    // Normalizacja do 0..2PI
    azNorth = (azNorth + 2 * Math.PI) % (2 * Math.PI)

    return HorizontalCoordinates(
        alt = Math.toDegrees(alt),
        az = Math.toDegrees(azNorth)
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun localSiderealTime(time: ZonedDateTime, longitudeDeg: Double): Double {
    val jd = getJulianDate(time)
    val d = jd - 2451545.0
    val T = d / 36525.0

    // GMST (Greenwich Mean Sidereal Time) - precyzyjny wzór IAU
    var gmst = 280.46061837 + 360.98564736629 * d + 0.000387933 * T * T - (T * T * T) / 38710000.0

    // Normalizacja do 0..360
    gmst %= 360.0
    if (gmst < 0) gmst += 360.0

    // LST = GMST + Longitude
    var lst = gmst + longitudeDeg
    lst %= 360.0
    if (lst < 0) lst += 360.0

    return Math.toRadians(lst)
}

@RequiresApi(Build.VERSION_CODES.O)
fun getJulianDate(time: ZonedDateTime): Double {
    val utc = time.withZoneSameInstant(ZoneOffset.UTC)

    var Y = utc.year
    var M = utc.monthValue
    val D = utc.dayOfMonth

    // Ułamek dnia (godziny, minuty, sekundy)
    val dayFraction = (utc.hour + utc.minute / 60.0 + utc.second / 3600.0) / 24.0

    if (M <= 2) {
        Y -= 1
        M += 12
    }

    val A = floor(Y / 100.0)
    val B = 2 - A + floor(A / 4.0)

    val JD = floor(365.25 * (Y + 4716)) +
            floor(30.6001 * (M + 1)) +
            D + B - 1524.5

    return JD + dayFraction
}
