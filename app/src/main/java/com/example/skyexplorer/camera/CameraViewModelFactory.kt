package com.example.skyexplorer.camera

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CameraViewModelFactory(
    private val cameraRepository: LocalRepository,
    val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CameraViewModel::class.java)) {
            return CameraViewModel(cameraRepository,
                application = application
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}