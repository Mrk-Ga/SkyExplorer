package com.example.skyexplorer.skymapscreen

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SkyMapViewModel : ViewModel() {

        private val _state = MutableStateFlow(SkyMapState())
        val state = _state.asStateFlow()

        fun handleIntent(intent: SkyMapIntent): SkyMapIntent? {
            return when (intent) {
                SkyMapIntent.NavigateToCamera -> SkyMapIntent.NavigateToCamera
                SkyMapIntent.NavigateToConstellations -> SkyMapIntent.NavigateToConstellations
            }
        }

}