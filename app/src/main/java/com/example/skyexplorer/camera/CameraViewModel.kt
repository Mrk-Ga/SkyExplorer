package com.example.skyexplorer.camera

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skyexplorer.components.ConstellationInfo
import com.example.skyexplorer.components.DialogWithImage
import com.example.skyexplorer.skymapscreen.Constellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.File
import java.security.AccessController.getContext
import java.text.SimpleDateFormat
import java.util.Locale

class CameraViewModel (
    private val repo: LocalRepository,
    application: Application
) : AndroidViewModel(application) {
    //private val _state = MutableStateFlow(CameraState())
    //val state = _state.asStateFlow()

/*
    fun chooseConstellationToPhoto(){
        val photoFile = File(
            context.externalMediaDirs.first(),
            SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US)
                .format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    exc.printStackTrace()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    onImageCaptured(Uri.fromFile(photoFile))
                }

            }
        )
    }

 */

    fun loadConstellationsInfo(): List<ConstellationInfo> {
        val context = getApplication<Application>().applicationContext

        val json = context.assets.open("constellations_info.json")
            .bufferedReader()
            .use { it.readText() }

        return Json.decodeFromString(json)
    }

    fun takePhoto(imageCapture: ImageCapture,context: Context, customName: String) {
        val timestamp = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US)
            .format(System.currentTimeMillis())

        val photoFile = File(
            context.externalMediaDirs.first(),
            "${customName}_${timestamp}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    exc.printStackTrace()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    savePhoto(Uri.fromFile(photoFile))
                }
            }
        )
    }

    fun savePhoto(uri: Uri) {
        val context = getApplication<Application>().applicationContext

        val customName = "Orion"   // np. nazwa gwiazdozbioru

        val timestamp = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US)
            .format(System.currentTimeMillis())

        val photoFile = File(
            context.externalMediaDirs.first(),
            "${customName}_$timestamp.jpg"
        )



        viewModelScope.launch {
            repo.insertPhoto(uri.toString())
        }
    }




    val photos = repo.getAllPhotos().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )


}