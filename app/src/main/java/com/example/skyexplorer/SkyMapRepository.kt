package com.example.skyexplorer

import com.example.skyexplorer.skymapscreen.Constellation
import com.example.skyexplorer.skymapscreen.Star

interface SkyMapRepository {
    suspend fun getLocalization(): Pair<Double, Double>?
    suspend fun loadStars(): List<Star>

    suspend fun loadConstellations(): List<Constellation>
}