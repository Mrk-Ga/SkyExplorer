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

data class Star(
    val id: Int,
    val name: String,
    val ra: Double,
    val dec: Double,
    val magnitude: Double,
    var azimuth: Double = 0.0,
    var altitude: Double = 0.0
)

data class Constellation(
    val name: String,
    val lines: List<List<Int>>
)