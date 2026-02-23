package com.example.skyexplorer

import com.example.skyexplorer.skymapscreen.Constellation
import com.example.skyexplorer.skymapscreen.SkyMapRepository
import com.example.skyexplorer.skymapscreen.Star
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSkyMapRepository : SkyMapRepository {

    override val stars = MutableStateFlow<List<Star>>(emptyList())
    override val constellations = MutableStateFlow<List<Constellation>>(emptyList())

    var location: Pair<Double, Double>? = null
    var loadStarsCalled = false
    var loadConstellationsCalled = false

    override suspend fun loadStars() {
        loadStarsCalled = true
    }

    override suspend fun loadConstellations() {
        loadConstellationsCalled = true
    }

    override suspend fun getLocalization(): Pair<Double, Double>? = location
}
