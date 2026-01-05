package com.example.skyexplorer.skymapscreen

import android.Manifest
import android.app.Application
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import com.example.skyexplorer.data.constellationLinesFilename
import com.example.skyexplorer.data.starsFilename
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.serialization.json.Json
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SkyMapRepositoryImpl(
    private val application: Application
) : SkyMapRepository {

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override suspend fun getLocalization() = getLocalizationSuspend(application)


    private var starsCache: List<Star>? = null
    private var constellationsCache: List<Constellation>? = null

    override suspend fun loadStars(): List<Star> {
        if (starsCache != null) {
            return starsCache!!
        }

        val json = application.assets
            .open(starsFilename)
            .bufferedReader()
            .use { it.readText() }
            .replace("NaN", "null")

        starsCache = Json.Default.decodeFromString(json)
        return starsCache!!
    }

    override suspend fun loadConstellations(): List<Constellation> {
        if (constellationsCache != null) {
            return constellationsCache!!
        }

        val json = application.assets
            .open(constellationLinesFilename)
            .bufferedReader()
            .use { it.readText() }

        constellationsCache = Json.Default.decodeFromString(json)
        return constellationsCache!!
    }

    @androidx.annotation.RequiresPermission(
        allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION]
    )
    suspend fun getLocalizationSuspend(application: Application): Pair<Double, Double>? = suspendCoroutine { cont ->
        //val context = getApplication<Application>().applicationContext
        val context = application.applicationContext
        val fused = LocationServices.getFusedLocationProviderClient(context)

        Log.d("LOCATION", "Pobieranie lokalizacji (try: current → last → updates)")

        // 1️⃣ najpierw getCurrentLocation()
        val request = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
            .setMaxUpdateAgeMillis(0)
            .build()

        fused.getCurrentLocation(request, null)
            .addOnSuccessListener { loc ->
                if (loc != null) {
                    Log.d("LOCATION", "getCurrentLocation: ${loc.latitude}, ${loc.longitude}")
                    cont.resume(Pair(loc.latitude, loc.longitude))
                } else {
                    Log.w("LOCATION", "⚠getCurrentLocation zwrócił null, próbuję lastLocation...")

                    // 2️⃣ fallback na lastLocation
                    fused.lastLocation
                        .addOnSuccessListener { last ->
                            if (last != null) {
                                Log.d(
                                    "LOCATION",
                                    "lastLocation: ${last.latitude}, ${last.longitude}"
                                )
                                cont.resume(Pair(last.latitude, last.longitude))
                            } else {
                                Log.w(
                                    "LOCATION",
                                    "⚠lastLocation == null, próbuję requestLocationUpdates..."
                                )

                                // 3️⃣ awaryjnie wymuszamy 1 aktualizację
                                val requestLoc = LocationRequest.Builder(
                                    Priority.PRIORITY_BALANCED_POWER_ACCURACY, 1000L
                                ).setMaxUpdates(1).build()

                                val callback = object : LocationCallback() {
                                    override fun onLocationResult(result: LocationResult) {
                                        val location = result.lastLocation
                                        if (location != null) {
                                            Log.d(
                                                "LOCATION",
                                                "requestLocationUpdates: ${location.latitude}, ${location.longitude}"
                                            )
                                            cont.resume(Pair(location.latitude, location.longitude))
                                        } else {
                                            Log.e(
                                                "LOCATION",
                                                "Nie udało się uzyskać lokalizacji nawet po requestLocationUpdates"
                                            )
                                            cont.resume(null)
                                        }
                                        fused.removeLocationUpdates(this)
                                    }
                                }

                                fused.requestLocationUpdates(
                                    requestLoc,
                                    callback,
                                    Looper.getMainLooper()
                                )
                            }
                        }
                        .addOnFailureListener {
                            Log.e("LOCATION", "lastLocation error: ${it.message}")
                            cont.resume(null)
                        }
                }
            }
            .addOnFailureListener {
                Log.e("LOCATION", "getCurrentLocation error: ${it.message}")
                cont.resume(null)
            }
    }
}