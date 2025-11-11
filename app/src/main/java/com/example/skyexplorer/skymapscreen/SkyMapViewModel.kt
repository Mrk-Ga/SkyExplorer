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
import android.app.Application
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.remember
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.copy
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Tasks
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@RequiresApi(Build.VERSION_CODES.O)
data class SkyUiState  constructor(
    val stars: List<Star> = emptyList(),
    //val constellations: List<Constellation> = emptyList(),
    val lat: Double? = null,
    val lon: Double? = null,
    val timeUtc: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC),
    val loading: Boolean = true,
    val error: String? = null
)


class SkyMapViewModel (application: Application): AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(SkyMapUiState())
    val uiState: StateFlow<SkyMapUiState> = _uiState

    private val _stars = MutableStateFlow<List<Star>>(emptyList())
    val stars: StateFlow<List<Star>> = _stars


    @RequiresApi(Build.VERSION_CODES.O)
    fun handleIntent(intent: SkyMapIntent){
        when (intent) {

            is SkyMapIntent.RequestNavigationPermission -> {
                // Tu możesz zainicjować logikę proszenia o uprawnienia
                _uiState.value = _uiState.value.copy(hasPermission = true)
            }

            is SkyMapIntent.NavigateToCamera -> {SkyMapIntent.NavigateToCamera}
            is SkyMapIntent.NavigateToConstellations -> {SkyMapIntent.NavigateToConstellations}

        }
    }
/*
    @SuppressLint("MissingPermission")
    suspend fun getLocalizationSuspend(): Pair<Double, Double>? = suspendCoroutine { cont ->
        val context = getApplication<Application>().applicationContext
        val fused = LocationServices.getFusedLocationProviderClient(context)

        Log.d("LOCATION", "⏳ Pobieranie lokalizacji...")
        fused.lastLocation
            .addOnSuccessListener { loc ->
                if (loc != null) {
                    Log.d("LOCATION", "✅ Lat=${loc.latitude}, Lon=${loc.longitude}")
                    cont.resume(Pair(loc.latitude, loc.longitude))
                } else {
                    Log.e("LOCATION", "❌ Brak ostatniej lokalizacji (null)")
                    cont.resume(null)
                }
            }
            .addOnFailureListener {
                Log.e("LOCATION", "❌ Błąd pobierania lokalizacji: ${it.message}")
                cont.resume(null)
            }
    }

 */
@SuppressLint("MissingPermission")
suspend fun getLocalizationSuspend(): Pair<Double, Double>? = suspendCoroutine { cont ->
    val context = getApplication<Application>().applicationContext
    val fused = LocationServices.getFusedLocationProviderClient(context)

    Log.d("LOCATION", "⏳ Pobieranie lokalizacji (try: current → last → updates)")

    // 1️⃣ najpierw getCurrentLocation()
    val request = CurrentLocationRequest.Builder()
        .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
        .setMaxUpdateAgeMillis(0)
        .build()

    fused.getCurrentLocation(request, null)
        .addOnSuccessListener { loc ->
            if (loc != null) {
                Log.d("LOCATION", "✅ getCurrentLocation: ${loc.latitude}, ${loc.longitude}")
                cont.resume(Pair(loc.latitude, loc.longitude))
            } else {
                Log.w("LOCATION", "⚠️ getCurrentLocation zwrócił null, próbuję lastLocation...")

                // 2️⃣ fallback na lastLocation
                fused.lastLocation
                    .addOnSuccessListener { last ->
                        if (last != null) {
                            Log.d("LOCATION", "✅ lastLocation: ${last.latitude}, ${last.longitude}")
                            cont.resume(Pair(last.latitude, last.longitude))
                        } else {
                            Log.w("LOCATION", "⚠️ lastLocation == null, próbuję requestLocationUpdates...")

                            // 3️⃣ awaryjnie wymuszamy 1 aktualizację
                            val requestLoc = LocationRequest.Builder(
                                Priority.PRIORITY_BALANCED_POWER_ACCURACY, 1000L
                            ).setMaxUpdates(1).build()

                            val callback = object : LocationCallback() {
                                override fun onLocationResult(result: LocationResult) {
                                    val location = result.lastLocation
                                    if (location != null) {
                                        Log.d("LOCATION", "✅ requestLocationUpdates: ${location.latitude}, ${location.longitude}")
                                        cont.resume(Pair(location.latitude, location.longitude))
                                    } else {
                                        Log.e("LOCATION", "❌ Nie udało się uzyskać lokalizacji nawet po requestLocationUpdates")
                                        cont.resume(null)
                                    }
                                    fused.removeLocationUpdates(this)
                                }
                            }

                            fused.requestLocationUpdates(requestLoc, callback, Looper.getMainLooper())
                        }
                    }
                    .addOnFailureListener {
                        Log.e("LOCATION", "❌ lastLocation error: ${it.message}")
                        cont.resume(null)
                    }
            }
        }
        .addOnFailureListener {
            Log.e("LOCATION", "❌ getCurrentLocation error: ${it.message}")
            cont.resume(null)
        }
}



    @RequiresApi(Build.VERSION_CODES.O)
    fun getTime(): ZonedDateTime {
        return ZonedDateTime.now(ZoneOffset.UTC)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION])
    suspend fun createVisibleStars(): List<Star>{
        val context = getApplication<Application>().applicationContext

        val loc = getLocalizationSuspend()
        if(loc == null){
            return emptyList()
        }
        val starsJsonString = context.assets.open("stars.json")
                .bufferedReader()
                .use { it.readText() }
                .replace("NaN", "null") // Dodaj tę linię, aby zamienić NaN na null

        val starsDecoded = Json.decodeFromString<List<Star>>(starsJsonString)
        Log.d("STARS", starsDecoded.toString())


        val stars = mutableListOf<Star>()

        starsDecoded.forEach { star ->
            val cords: HorizontalCoordinates
            val timeUtc = getTime()
            cords = raDecToAltAz(star.ra*15.0, star.dec, loc.first, loc.second, timeUtc)
            star.alt = cords.alt
            star.az = cords.az

            star.alt?.let {

                if(it > 0){
                    stars.add(star)
                }
            }
        }

        Log.d("STARS", stars.toString())

        return stars




    }

    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun loadStars() {
        Log.d("DEBUG", "Wywołano loadStars()")

        viewModelScope.launch {
            Log.d("DEBUG", "Rozpoczynam createVisibleStars() w korutynie")

            val result = createVisibleStars()
            Log.d("DEBUG", "Załadowano ${result.size} widocznych gwiazd")

            _stars.value = result
        }
    }



}

