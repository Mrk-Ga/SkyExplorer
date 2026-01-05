package com.example.skyexplorer.camera

interface CameraRepository {
    suspend fun insertPhoto(uri: String)
    fun loadConstellationsInfo(): List<ConstellationInfo>
}
