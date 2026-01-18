package com.example.skyexplorer.photoGallery


import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.skyexplorer.PhotoEntity
import com.example.skyexplorer.camera.LocalRepository
import com.example.skyexplorer.camera.ConstellationInfo
import com.example.skyexplorer.camera.PhotoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.collections.filter
import com.example.skyexplorer.data.*



class PhotoGalleryViewModel(
    application: Application,
    //val constellationName: String
    //private val repo: PhotoRepository
) : AndroidViewModel(application) {

    private val repo = LocalRepository(application)


    // Flow zdjęć wyświetlanych w galerii
    val photos = repo.getAllPhotos()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )


    fun deletePhoto(photo: PhotoEntity) {
        viewModelScope.launch {
            repo.deletePhoto(photo)
        }
    }

    fun photoFilter(photoList: List<PhotoEntity>, constellationName: String): List<PhotoEntity> {
        val regex = Regex(".*/${Regex.escape(constellationName).replace(" ", "_")}_.*\\.${photoFilesFormat}$")

        //Log.d("PhotoGalleryViewModel", "PhotoList: $photoList")

        return photoList.filter { regex.matches(it.uri) }
    }

    fun loadConstellationsInfo(constellationName: String): ConstellationInfo{
        val context = getApplication<Application>().applicationContext

        val json = context.assets.open(constellationInfoFilename)
            .bufferedReader()
            .use { it.readText() }

        val decoded = Json.decodeFromString<List<ConstellationInfo>>(json)



        return decoded.find { it.latin == constellationName }!!

    }

    fun shareImage(
        context: Context,
        imageFile: File,
        text: String
    ) {
        val shareUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, shareUri)
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(
            Intent.createChooser(intent, sharePhotoTitle)
        )
    }
}