package com.example.skyexplorer

import android.app.Application
import com.example.skyexplorer.data.constellationLinesFilename
import com.example.skyexplorer.data.starsFilename
import com.example.skyexplorer.skymapscreen.SkyMapModel
import kotlinx.serialization.json.Json
import com.example.skyexplorer.skymapscreen.Star
import com.example.skyexplorer.skymapscreen.Constellation
import com.example.skyexplorer.SkyMapRepository


class SkyMapRepositoryImpl(
    private val application: Application
) : SkyMapRepository {
    override suspend fun getLocalization() =
        SkyMapModel().getLocalizationSuspend(application)

    override suspend fun loadStars(): List<Star> {
        val json = application.assets.open(starsFilename)
            .bufferedReader()
            .readText()
            .replace("NaN", "null")
        return Json.decodeFromString(json)
    }

    override suspend fun loadConstellations(): List<Constellation> {
        val json = application.assets.open(constellationLinesFilename)
            .bufferedReader()
            .readText()
            .replace("NaN", "null")
        return Json.decodeFromString(json)
    }
}
