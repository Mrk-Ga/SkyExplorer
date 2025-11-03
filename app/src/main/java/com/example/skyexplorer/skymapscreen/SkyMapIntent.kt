package com.example.skyexplorer.skymapscreen

import android.content.Context

sealed class SkyMapIntent{
    object NavigateToCamera : SkyMapIntent()
    object NavigateToConstellations: SkyMapIntent()

    object RequestNavigationPermission: SkyMapIntent()

    //class LoadStars: SkyMapIntent()
    //class LoadConstellations: SkyMapIntent()
    data class UpdateLocation(val lat: Double, val lon: Double) : SkyMapIntent()

}

data class SkyMapUiState(
    val hasPermission: Boolean = false,
    val webUrl: String = "https://stellarium-web.org"
)
