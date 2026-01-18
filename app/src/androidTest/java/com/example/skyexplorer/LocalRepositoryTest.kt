package com.example.skyexplorer

import android.content.Context
import android.net.Uri
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.skyexplorer.camera.LocalRepository
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import java.io.File
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse


@RunWith(AndroidJUnit4::class)
class LocalRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var repo: LocalRepository
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()

        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        repo = LocalRepository(context)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertPhoto_savesPhotoInDatabase() = runTest {
        repo.insertPhoto("file://test.jpg")

        val photos = repo.getAllPhotos().first()

        assertEquals(1, photos.size)
        assertEquals("file://test.jpg", photos.first().uri)
    }


    //ten test nie przechodzi
    @Test
    fun deletePhoto_removesDbEntry() = runTest {
        val uri = "file:///tmp/test.jpg"

        repo.insertPhoto(uri)

        val inserted = db.photoDao().getAllPhotosOnce()
        assertTrue(inserted.isNotEmpty())

        val photoFromDb = inserted.first()

        repo.deletePhoto(photoFromDb)

        val afterDelete = db.photoDao().getAllPhotosOnce()

        assertTrue(afterDelete.isEmpty())
    }




}