package com.example.skyexplorer.camera


import android.net.Uri
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await


//class with data format in firebase
data class PhotoData(
    val url: String = "",
    val timestamp: Long = System.currentTimeMillis()
)


//database class
class FirebaseRepository {

    //private val storage = FirebaseStorage.getInstance()
    //private val db = FirebaseFirestore.getInstance()

    //function to upload photo to firebase
    /*
    suspend fun uploadPhoto(uri: Uri): Boolean {
        return try {
            val ref = storage.reference.child("photos/${System.currentTimeMillis()}.jpg")
            ref.putFile(uri).await()

            val downloadUrl = ref.downloadUrl.await().toString()

            val photoData = PhotoData(url = downloadUrl)
            db.collection("photos").add(photoData).await()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

     */
}
