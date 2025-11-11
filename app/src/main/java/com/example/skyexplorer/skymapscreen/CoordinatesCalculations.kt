package com.example.skyexplorer.skymapscreen

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.ZonedDateTime
import kotlin.math.*

data class HorizontalCoordinates(val alt: Double, val az: Double)

@RequiresApi(Build.VERSION_CODES.O)
fun raDecToAltAz(raDeg: Double, decDeg: Double, latDeg: Double, lonDeg: Double, dateTime: ZonedDateTime): HorizontalCoordinates {
    val ra = Math.toRadians(raDeg)
    val dec = Math.toRadians(decDeg)
    val lat = Math.toRadians(latDeg)
    val lst = localSiderealTime(dateTime, lonDeg)
    val ha = lst - ra
    val alt = asin(sin(dec) * sin(lat) + cos(dec) * cos(lat) * cos(ha))
    val az = atan2(-sin(ha) * cos(dec), sin(dec) - sin(alt) * sin(lat))
    return HorizontalCoordinates(Math.toDegrees(alt), (Math.toDegrees(az) + 360) % 360)
}


@RequiresApi(Build.VERSION_CODES.O)
fun localSiderealTime(time: java.time.ZonedDateTime, longitudeDeg: Double): Double {
    val jd = getJulianDate(time)
    val t = (jd - 2451545.0) / 36525.0
    var lst = 280.46061837 + 360.98564736629 * (jd - 2451545) +
            0.000387933 * t.pow(2) - t.pow(3) / 38710000 + longitudeDeg
    lst %= 360.0
    if (lst < 0) lst += 360.0
    return Math.toRadians(lst)
}

@RequiresApi(Build.VERSION_CODES.O)
fun getJulianDate(time: java.time.ZonedDateTime): Double {
    val utc = time.withZoneSameInstant(java.time.ZoneOffset.UTC)
    val year = utc.year
    val month = utc.monthValue
    val day = utc.dayOfMonth +
            (utc.hour + utc.minute / 60.0 + utc.second / 3600.0) / 24.0

    var y = year
    var m = month
    if (m <= 2) {
        y -= 1
        m += 12
    }
    val a = floor(y / 100.0)
    val b = 2 - a + floor(a / 4.0)
    return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
}
