package com.example.skyexplorer.photoGallery


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.skyexplorer.PhotoEntity
import com.example.skyexplorer.camera.LocalRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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
}