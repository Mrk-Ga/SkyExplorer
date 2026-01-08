package com.example.skyexplorer.camera

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.skyexplorer.data.constellationInfoFilename
import com.example.skyexplorer.data.dateFormat
import com.example.skyexplorer.data.photoFilesFormat
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class CameraViewModel (
    private val repo: LocalRepository,
    application: Application
) : AndroidViewModel(application) {
    //private val _state = MutableStateFlow(CameraState())
    //val state = _state.asStateFlow()


    fun loadConstellationsInfo(): List<ConstellationInfo> {
        val context = getApplication<Application>().applicationContext

        val json = context.assets.open(constellationInfoFilename)
            .bufferedReader()
            .use { it.readText() }

        return Json.decodeFromString(json)
    }
    var pendingPhotoUri by mutableStateOf<Uri?>(null)

    fun takePhoto(
        imageCapture: ImageCapture,
        context: Context,
        onPhotoTaken: () -> Unit
    ) {
        val photoFile = createTempPhotoFile(context)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(result: ImageCapture.OutputFileResults) {
                    pendingPhotoUri = Uri.fromFile(photoFile)
                    onPhotoTaken()
                }

                override fun onError(exc: ImageCaptureException) {
                    exc.printStackTrace()
                }
            }
        )
    }

    fun confirmConstellation(constellation: String) {
        pendingPhotoUri?.let { uri ->
            viewModelScope.launch {
                val oldFile = File(uri.path!!)

                val timestamp = SimpleDateFormat(
                    dateFormat,
                    Locale.US
                ).format(System.currentTimeMillis())

                val newFile = File(
                    getApplication<Application>()
                        .applicationContext
                        .externalMediaDirs
                        .first(),
                    "${constellation}_${timestamp}.${photoFilesFormat}"
                )

                oldFile.copyTo(newFile, overwrite = true)
                oldFile.delete()

                repo.insertPhoto(Uri.fromFile(newFile).toString())
            }
        }
        pendingPhotoUri = null
    }

    fun cancelPhoto() {
        pendingPhotoUri = null
    }

    fun savePhoto(uri: Uri) {


        viewModelScope.launch {
            repo.insertPhoto(uri.toString())
        }
    }

    private fun createTempPhotoFile(context: Context): File {
        val storageDir = context.cacheDir
        return File.createTempFile(
            "photo_",
            photoFilesFormat,
            storageDir
        )
    }




}