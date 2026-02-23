package com.example.skyexplorer.skymapscreen

import kotlinx.coroutines.flow.StateFlow

interface SkyMapRepository {
    suspend fun getLocalization(): Pair<Double, Double>?
    suspend fun loadStars()
    suspend fun loadConstellations()

    val stars: StateFlow<List<Star>>
    val constellations: StateFlow<List<Constellation>>
}