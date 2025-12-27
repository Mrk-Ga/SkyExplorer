package com.example.skyexplorer.skymapscreen

interface SkyMapRepository {
    suspend fun getLocalization(): Pair<Double, Double>?
    suspend fun loadStars(): List<Star>

    suspend fun loadConstellations(): List<Constellation>
}