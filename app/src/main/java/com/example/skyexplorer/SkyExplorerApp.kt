package com.example.skyexplorer


import android.app.Application
//import com.google.firebase.FirebaseApp

class SkyExplorerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        //Firebase initialization - only once at start of code
        //FirebaseApp.initializeApp(this)
    }
}