package com.example.skyexplorer.camera

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CameraViewModel : ViewModel() {
    private val _state = MutableStateFlow(CameraState())
    val state = _state.asStateFlow()

    //create of database instance
    //private val repo = FirebaseRepository()

    fun uploadPhoto(uri: Uri) {
        /*
        viewModelScope.launch {
            repo.uploadPhoto(uri)
        }

         */
    }


}