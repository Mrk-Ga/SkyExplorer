package com.example.skyexplorer.data


import com.example.skyexplorer.skymapscreen.Star
import com.example.skyexplorer.skymapscreen.Constellation

class SkyRepository {

    // ===== CACHE DANYCH DO UI =====

    var visibleStars: List<Star>? = null
        private set

    var constellations: List<Constellation>? = null
        private set

    // ===== KLUCZE WAŻNOŚCI =====

    private var lastMinute: Long = -1
    private var lastLat: Double? = null
    private var lastLon: Double? = null

    // ===== API =====

    fun hasValidStars(
        minute: Long,
        lat: Double,
        lon: Double
    ): Boolean {
        return visibleStars != null &&
                minute == lastMinute &&
                lat == lastLat &&
                lon == lastLon
    }

    fun saveStars(
        stars: List<Star>,
        minute: Long,
        lat: Double,
        lon: Double
    ) {
        visibleStars = stars
        lastMinute = minute
        lastLat = lat
        lastLon = lon
    }

    fun saveConstellations(list: List<Constellation>) {
        constellations = list
    }
}
