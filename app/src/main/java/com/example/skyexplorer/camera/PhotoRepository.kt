package com.example.skyexplorer.camera

import com.example.skyexplorer.PhotoEntity
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    fun getAllPhotos(): Flow<List<PhotoEntity>>
    suspend fun insertPhoto(uri: String)
    suspend fun deletePhoto(photo: PhotoEntity)
}
