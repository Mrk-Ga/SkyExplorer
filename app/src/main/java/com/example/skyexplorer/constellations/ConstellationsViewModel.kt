package com.example.skyexplorer.constellations

import androidx.lifecycle.ViewModel
import com.example.skyexplorer.camera.CameraState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ConstellationsViewModel: ViewModel() {

    private val _state = MutableStateFlow(ConstellationsState())
    val state = _state.asStateFlow()
}