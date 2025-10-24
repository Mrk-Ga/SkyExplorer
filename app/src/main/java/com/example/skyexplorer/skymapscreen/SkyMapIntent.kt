package com.example.skyexplorer.skymapscreen

sealed class SkyMapIntent{
    object NavigateToCamera : SkyMapIntent()
    object NavigateToConstellations: SkyMapIntent()
}