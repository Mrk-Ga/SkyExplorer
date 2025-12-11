package com.example.skyexplorer.skymapscreen

sealed class SkyMapIntent{
    object NavigateToCamera : SkyMapIntent()
    object NavigateToConstellations: SkyMapIntent()

    object RequestNavigationPermission: SkyMapIntent()

}

data class SkyMapUiState(
    val hasPermission: Boolean = false,
    val webUrl: String = "https://stellarium-web.org",
    val loading: Boolean
)
