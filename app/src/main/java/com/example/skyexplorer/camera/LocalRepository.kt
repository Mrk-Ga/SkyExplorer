package com.example.skyexplorer.camera
import android.content.Context
import androidx.room.Room
import com.example.skyexplorer.AppDatabase
import com.example.skyexplorer.PhotoEntity
import kotlinx.coroutines.flow.Flow

class LocalRepository(context: Context) {
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "skyexplorer_db"
    ).build()



    private val photoDao = db.photoDao()

    fun getAllPhotos(): Flow<List<PhotoEntity>> = photoDao.getAllPhotos()

    suspend fun insertPhoto(uri: String) {
        photoDao.insertPhoto(PhotoEntity(uri = uri))
    }

    suspend fun deletePhoto(photo: PhotoEntity) {
        photoDao.deletePhoto(photo)
    }
}
