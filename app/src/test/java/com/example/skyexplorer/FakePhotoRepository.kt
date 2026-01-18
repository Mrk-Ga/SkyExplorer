package com.example.skyexplorer

import com.example.skyexplorer.camera.PhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakePhotoRepositoryCameraTest : PhotoRepository {


    val insertedUris = mutableListOf<String>()
    val photos = MutableStateFlow<List<PhotoEntity>>(emptyList())

    override fun getAllPhotos(): Flow<List<PhotoEntity>> = photos

    override suspend fun insertPhoto(uri: String) {
        insertedUris.add(uri)
        photos.value += PhotoEntity(uri = uri)
    }

    override suspend fun deletePhoto(photo: PhotoEntity) {
        photos.value -= photo
    }
}

class FakePhotoRepositoryGalleryTest : PhotoRepository {

    val deleted = mutableListOf<PhotoEntity>()
    private val _photos = MutableStateFlow<List<PhotoEntity>>(emptyList())

    override fun getAllPhotos(): Flow<List<PhotoEntity>> = _photos

    override suspend fun insertPhoto(uri: String) {
        _photos.value += PhotoEntity(uri = uri)
    }

    override suspend fun deletePhoto(photo: PhotoEntity) {
        deleted.add(photo)
        _photos.value -= photo
    }


}