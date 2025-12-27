package com.example.skyexplorer.skymapscreen

import SkyMapViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skyexplorer.skymapscreen.SkyMapRepository
import com.example.skyexplorer.data.SkyRepository // Assuming you have a repository

class SkyMapViewModelFactory(private val repository: SkyMapRepository) : ViewModelProvider.Factory {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SkyMapViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SkyMapViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}