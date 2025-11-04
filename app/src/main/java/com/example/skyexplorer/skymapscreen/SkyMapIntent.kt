package com.example.skyexplorer.skymapscreen

import android.content.Context

sealed class SkyMapIntent{
    object NavigateToCamera : SkyMapIntent()
    object NavigateToConstellations: SkyMapIntent()

    object RequestNavigationPermission: SkyMapIntent()

}

data class SkyMapUiState(
    val hasPermission: Boolean = false,
    val webUrl: String = "https://stellarium-web.org"
)
