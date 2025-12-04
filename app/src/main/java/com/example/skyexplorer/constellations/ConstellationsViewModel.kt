package com.example.skyexplorer.constellations

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skyexplorer.camera.LocalRepository
import com.example.skyexplorer.components.ConstellationInfo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.json.Json

class ConstellationsViewModel(application: Application): AndroidViewModel(application) {

    //private val _state = MutableStateFlow(ConstellationsState())
    //val state = _state.asStateFlow()

    private val repo = LocalRepository(application)

    // Flow zdjęć wyświetlanych w galerii
    val photos = repo.getAllPhotos()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun loadConstellationsInfo(): List<ConstellationInfo> {
        val context = getApplication<Application>().applicationContext

        val json = context.assets.open("constellations_info.json")
            .bufferedReader()
            .use { it.readText() }

        return Json.decodeFromString(json)
    }
}