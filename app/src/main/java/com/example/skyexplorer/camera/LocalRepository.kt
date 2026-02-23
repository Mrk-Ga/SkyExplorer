package com.example.skyexplorer.camera
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.room.Room
import com.example.skyexplorer.AppDatabase
import com.example.skyexplorer.PhotoEntity
import kotlinx.coroutines.flow.Flow
import java.io.File
import androidx.core.net.toUri

class LocalRepository(context: Context):PhotoRepository {
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "skyexplorer_db"
    ).build()

    private val photoDao = db.photoDao()

    override fun getAllPhotos(): Flow<List<PhotoEntity>> = photoDao.getAllPhotos()

    override suspend fun insertPhoto(uri: String) {
        photoDao.insertPhoto(PhotoEntity(uri = uri))
    }

    override suspend fun deletePhoto(photo: PhotoEntity) {
        Log.d("DELETE PHOTO", photo.toString())

        deleteFileIfExists(photo.uri)

        photoDao.deletePhoto(photo)
    }



    private fun deleteFileIfExists(uriString: String) {
        try {
            val uri = uriString.toUri()

            if (uri.scheme == "file") {
                val file = File(uri.path!!)

                if (file.exists()) {
                    val deleted = file.delete()
                    Log.d("DELETE FILE", "Deleted=$deleted path=${file.path}")
                } else {
                    Log.w("DELETE FILE", "File does not exist: ${file.path}")
                }
            } else {
                Log.w("DELETE FILE", "Unsupported URI scheme: $uriString")
            }

        } catch (e: Exception) {
            Log.e("DELETE FILE", "Error deleting file", e)
        }
    }

}
