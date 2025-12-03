package com.example.skyexplorer.skymapscreen

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.math.*

data class HorizontalCoordinates(val alt: Double, val az: Double)

@RequiresApi(Build.VERSION_CODES.O)
fun raDecToAltAz(
    raHours: Double,
    decDeg: Double,
    latDeg: Double,
    lonDeg: Double,
    dateTime: ZonedDateTime
): HorizontalCoordinates {

    val ra = Math.toRadians(raHours * 15.0)
    val dec = Math.toRadians(decDeg)
    val lat = Math.toRadians(latDeg)

    var lst = localSiderealTime(dateTime, lonDeg)

    var ha = lst - ra
    ha = ((ha % (2 * Math.PI)) + 2 * Math.PI) % (2 * Math.PI)

    val sinAlt = sin(dec) * sin(lat) + cos(dec) * cos(lat) * cos(ha)
    val alt = asin(sinAlt)

    val y = sin(ha)
    val x = cos(ha) * sin(lat) - tan(dec) * cos(lat)
    var az = atan2(y, x)
    if (az < 0) az += 2 * Math.PI

    var azDeg = Math.toDegrees(az)

    // POPRAWKA SYSTEMU AZYMUTU
    azDeg = (azDeg + 180) % 360
    azDeg = (360 - azDeg) % 360

    return HorizontalCoordinates(
        Math.toDegrees(alt),
        azDeg
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun localSiderealTime(time: ZonedDateTime, longitudeDeg: Double): Double {

    // PRZELICZAMY CZAS NA UTC
    val utcTime = time.withZoneSameInstant(java.time.ZoneOffset.UTC)

    val jd = getJulianDate(utcTime)
    val t = (jd - 2451545.0) / 36525.0

    var lst = 280.46061837 +
            360.98564736629 * (jd - 2451545) +
            0.000387933 * t * t -
            t * t * t / 38710000 +
            longitudeDeg

    lst %= 360.0
    if (lst < 0) lst += 360.0

    return Math.toRadians(lst)
}

@RequiresApi(Build.VERSION_CODES.O)
fun getJulianDate(time: ZonedDateTime): Double {
    // czas UTC
    val utc = time.withZoneSameInstant(ZoneOffset.UTC)

    val year = utc.year
    val month = utc.monthValue
    val day = utc.dayOfMonth
    val hour = utc.hour + utc.minute / 60.0 + utc.second / 3600.0

    var Y = year
    var M = month

    if (M <= 2) {
        Y -= 1
        M += 12
    }

    val A = floor(Y / 100.0)
    val B = 2 - A + floor(A / 4.0)

    val JD0 = floor(365.25 * (Y + 4716)) +
            floor(30.6001 * (M + 1)) +
            day + B - 1524.5

    return JD0 + hour / 24.0
}
