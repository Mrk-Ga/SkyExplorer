package com.example.skyexplorer


import android.app.Application
import com.example.skyexplorer.camera.LocalRepository

class SkyExplorerApp : Application() {

    lateinit var cameraRepository: LocalRepository
        private set

    override fun onCreate() {
        super.onCreate()

        cameraRepository = LocalRepository(this)
    }
}