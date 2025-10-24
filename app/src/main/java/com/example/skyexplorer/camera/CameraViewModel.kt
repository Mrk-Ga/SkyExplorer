package com.example.skyexplorer.camera

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CameraViewModel : ViewModel() {
    private val _state = MutableStateFlow(CameraState())
    val state = _state.asStateFlow()
}