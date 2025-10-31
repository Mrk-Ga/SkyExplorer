package com.example.skyexplorer.skymapscreen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.util.copy
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.flow.StateFlow


class SkyMapViewModel : ViewModel() {

    private val _state = MutableStateFlow(SkyMapState())
    val state = _state.asStateFlow()

    @SuppressLint("MissingPermission")
    fun fetchCurrentLocation(context: Context, onResult: (Double, Double) -> Unit) {
        val client = LocationServices.getFusedLocationProviderClient(context)
        client.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                onResult(location.latitude, location.longitude)
            }
        }
    }

    private val _uiState = MutableStateFlow(SkyMapUiState())
    val uiState: StateFlow<SkyMapUiState> = _uiState





    @RequiresApi(Build.VERSION_CODES.O)
    fun handleIntent(intent: SkyMapIntent){
        when (intent) {

            is SkyMapIntent.RequestNavigationPermission -> {
                // Tu możesz zainicjować logikę proszenia o uprawnienia
                _uiState.value = _uiState.value.copy(hasPermission = true)
            }

            is SkyMapIntent.UpdateLocation -> {
                val time = java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC)
                    .toString().substringBeforeLast("[")
                val newUrl =
                    "https://stellarium-web.org/?lat=${intent.lat}&lon=${intent.lon}&date=$time&fov=100"
                _uiState.value = _uiState.value.copy(webUrl = newUrl)
            }

            is SkyMapIntent.NavigateToCamera -> {SkyMapIntent.NavigateToCamera}
            is SkyMapIntent.NavigateToConstellations -> {SkyMapIntent.NavigateToConstellations}

        }
    }


    fun loadStars(context: Context): List<Star> {
        val json = context.assets.open("stars.json").bufferedReader().use { it.readText() }

        val type = object : TypeToken<List<Star>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun loadConstellations(context: Context): List<Constellation> {
        val json = context.assets.open("constellations.json").bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<Constellation>>() {}.type
        return Gson().fromJson(json, type)
    }

}