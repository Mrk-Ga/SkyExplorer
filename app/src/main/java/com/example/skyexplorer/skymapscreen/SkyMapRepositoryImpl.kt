package com.example.skyexplorer.skymapscreen

import android.app.Application
import com.example.skyexplorer.data.constellationLinesFilename
import com.example.skyexplorer.data.starsFilename
import kotlinx.serialization.json.Json

class SkyMapRepositoryImpl(
    private val application: Application
) : SkyMapRepository {

    override suspend fun getLocalization() =
        SkyMapModel().getLocalizationSuspend(application)


    private var starsCache: List<Star>? = null
    private var constellationsCache: List<Constellation>? = null

    override suspend fun loadStars(): List<Star> {
        if (starsCache != null) {
            return starsCache!!
        }

        val json = application.assets
            .open(starsFilename)
            .bufferedReader()
            .use { it.readText() }
            .replace("NaN", "null")

        starsCache = Json.Default.decodeFromString(json)
        return starsCache!!
    }

    override suspend fun loadConstellations(): List<Constellation> {
        if (constellationsCache != null) {
            return constellationsCache!!
        }

        val json = application.assets
            .open(constellationLinesFilename)
            .bufferedReader()
            .use { it.readText() }

        constellationsCache = Json.Default.decodeFromString(json)
        return constellationsCache!!
    }
}