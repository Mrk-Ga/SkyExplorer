package com.example.skyexplorer.photoGallery


import android.app.Application
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.skyexplorer.PhotoEntity
import com.example.skyexplorer.camera.LocalRepository
import com.example.skyexplorer.components.ConstellationInfo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.collections.filter


class PhotoGalleryViewModel(
    application: Application,
    //val constellationName: String
) : AndroidViewModel(application) {

    private val repo = LocalRepository(application)


    // Flow zdjęć wyświetlanych w galerii
    val photos = repo.getAllPhotos()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )


    // (opcjonalnie) Usuwanie zdjęcia z bazy
    fun deletePhoto(photo: PhotoEntity) {
        viewModelScope.launch {
            repo.deletePhoto(photo)
        }
    }

    fun photoFilter(photoList: List<PhotoEntity>, constellationName: String): List<PhotoEntity> {
        val regex = Regex(".*/${Regex.escape(constellationName).replace(" ", "%20")}_.*\\.jpg$")

        Log.d("PhotoGalleryViewModel", "PhotoList: $photoList")

        return photoList.filter { regex.matches(it.uri) }
    }

    fun loadConstellationsInfo(constellationName: String): ConstellationInfo{
        val context = getApplication<Application>().applicationContext

        val json = context.assets.open("constellations_info.json")
            .bufferedReader()
            .use { it.readText() }

        val decoded = Json.decodeFromString<List<ConstellationInfo>>(json)



        return decoded.find { it.latin == constellationName }!!

    }
}