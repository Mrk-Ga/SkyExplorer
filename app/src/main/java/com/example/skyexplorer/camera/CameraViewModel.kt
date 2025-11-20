package com.example.skyexplorer.camera

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.security.AccessController.getContext

class CameraViewModel (
    private val repo: LocalRepository
) : ViewModel() {
    private val _state = MutableStateFlow(CameraState())
    val state = _state.asStateFlow()



    fun savePhoto(uri: Uri) {
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