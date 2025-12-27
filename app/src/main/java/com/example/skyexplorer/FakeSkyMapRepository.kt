package com.example.skyexplorer

import com.example.skyexplorer.skymapscreen.Constellation
import com.example.skyexplorer.skymapscreen.Star

class FakeSkyMapRepository(
    private val location: Pair<Double, Double>? = 52.0 to 21.0,
    private val stars: List<Star> = emptyList(),
    private val constellations: List<Constellation> = emptyList(),
    private val throwError: Boolean = false
) : SkyMapRepository {

    override suspend fun getLocalization(): Pair<Double, Double>? {
        if (throwError) throw RuntimeException("Location error")
        return location
    }

    override suspend fun loadStars(): List<Star> {
        if (throwError) throw RuntimeException("Stars error")
        return stars
    }

    override suspend fun loadConstellations(): List<Constellation> {
        if (throwError) throw RuntimeException("Constellations error")
        return constellations
    }
}
