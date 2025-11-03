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
import androidx.lifecycle.viewModelScope
import androidx.room.util.copy
import com.google.android.gms.tasks.Tasks
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.ZoneOffset
import java.time.ZonedDateTime

@RequiresApi(Build.VERSION_CODES.O)
data class SkyUiState  constructor(
    val stars: List<Star> = emptyList(),
    val constellations: List<Constellation> = emptyList(),
    val lat: Double? = null,
    val lon: Double? = null,
    val timeUtc: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC),
    val loading: Boolean = true,
    val error: String? = null
)


class SkyMapViewModel : ViewModel() {

    private val _state = MutableStateFlow(SkyMapState())
    val state = _state.asStateFlow()
/*
    @SuppressLint("MissingPermission")
    fun fetchCurrentLocation(context: Context, onResult: (Double, Double) -> Unit) {
        val client = LocationServices.getFusedLocationProviderClient(context)
        client.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                onResult(location.latitude, location.longitude)
            }
        }
    }

 */

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

    @RequiresApi(Build.VERSION_CODES.O)
    private val _ui = MutableStateFlow(SkyUiState())
    @RequiresApi(Build.VERSION_CODES.O)
    val ui: StateFlow<SkyUiState> = _ui

    /** Jednorazowe wczytanie JSON-ów z assets */
    private fun loadAssets(context: Context): Pair<List<Star>, List<Constellation>> {
        val gson = Gson()

        val starsJson = context.assets.open("stars.json").bufferedReader().use { it.readText() }
        val starsType = object : TypeToken<List<Star>>() {}.type
        val stars: List<Star> = gson.fromJson(starsJson, starsType)

        val constJson = context.assets.open("constellations.json").bufferedReader().use { it.readText() }
        val constType = object : TypeToken<List<Constellation>>() {}.type
        val constellations: List<Constellation> = gson.fromJson(constJson, constType)

        return stars to constellations
    }

    /** Publiczne API — start liczenia pozycji z GPS i czasu */
    @RequiresApi(Build.VERSION_CODES.O)
    fun initializeAndCompute(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val (starsRaw, constellations) = loadAssets(context)
                val (lat, lon) = getLastKnownLocation(context) ?: (52.2297 to 21.0122) // fallback: Warszawa
                val now = ZonedDateTime.now(ZoneOffset.UTC)

                val starsPos = computePositions(starsRaw, lat, lon, now)

                _ui.value = SkyUiState(
                    stars = starsPos,
                    constellations = constellations,
                    lat = lat, lon = lon,
                    timeUtc = now,
                    loading = false
                )
            } catch (e: Exception) {
                _ui.value = _ui.value.copy(loading = false, error = e.message ?: "Unknown error")
            }
        }
    }

    /** Przelicza pozycje wszystkich gwiazd dla podanej lokalizacji/czasu */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun computePositions(
        stars: List<Star>,
        lat: Double,
        lon: Double,
        tUtc: ZonedDateTime
    ): List<Star> {
        return stars.map { s ->
            val (altDeg, azDeg) = raDecToAltAzDeg(
                raDeg = s.ra,
                decDeg = s.dec,
                latDeg = lat,
                lonDeg = lon,
                tUtc = tUtc
            )

            val altRefr = applyAtmosphericRefraction(altDeg)
            s.copy(alt = altRefr, az = azDeg)
        }.filter { it.alt > 0.0 } // rysujemy tylko nad horyzontem
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation(context: Context): Pair<Double, Double>? {
        // Upewnij się, że masz runtime permission na ACCESS_COARSE/FINE_LOCATION
        val client = LocationServices.getFusedLocationProviderClient(context)
        val task = client.lastLocation
        val loc = task.runCatching { Tasks.await(task) }.getOrNull()
        return loc?.let { it.latitude to it.longitude }
    }



}