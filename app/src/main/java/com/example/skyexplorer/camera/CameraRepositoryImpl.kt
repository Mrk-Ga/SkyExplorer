package com.example.skyexplorer.camera

import android.app.Application
import kotlinx.serialization.json.Json
import com.example.skyexplorer.data.*

class CameraRepositoryImpl(
    private val application: Application,
    private val localRepository: LocalRepository
) : CameraRepository {

    override fun loadConstellationsInfo(): List<ConstellationInfo> {
        val json = application.assets
            .open(constellationInfoFilename)
            .bufferedReader()
            .use { it.readText() }

        return Json.decodeFromString(json)
    }

    override suspend fun insertPhoto(uri: String) {
        localRepository.insertPhoto(uri)
    }


}
