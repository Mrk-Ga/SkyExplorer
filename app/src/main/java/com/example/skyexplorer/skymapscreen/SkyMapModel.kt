package com.example.skyexplorer.skymapscreen

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Tasks
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SkyMapModel(
    //val context: Context
    //private val api: << połączenie z bazą danych
){


}


@kotlinx.serialization.Serializable
data class Star(
    val id: Int,
    val name: String,
    val ra: Double,
    val dec: Double,
    val magnitude: Double,
    val sptype: String,
    var alt: Double? = null,
    var az: Double? = null
)

@kotlinx.serialization.Serializable
data class Constellation(
    val id: String,
    val name: String,
    val segments: List<List<Int>>
)