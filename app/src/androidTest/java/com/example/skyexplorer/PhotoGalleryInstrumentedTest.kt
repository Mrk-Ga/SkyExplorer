package com.example.skyexplorer

import android.app.Application
import android.provider.ContactsContract
import androidx.test.core.app.ApplicationProvider
import com.example.skyexplorer.photoGallery.PhotoGalleryViewModel
import org.junit.Before
import org.junit.Test
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class PhotoGalleryInstrumentedTest {

    private lateinit var viewModel: PhotoGalleryViewModel

    @Before
    fun setup() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        viewModel = PhotoGalleryViewModel(
            app,
            repo = FakePhotoRepositoryGalleryTest()
        )
    }

    @Test
    fun loadConstellationsInfo_returnsCorrectConstellation() {
        val info = viewModel.loadConstellationsInfo("Orion")

        assertEquals("Orion", info.latin)
        assertTrue(info.polish.isNotBlank())
    }

    @Test(expected = NullPointerException::class)
    fun loadConstellationsInfo_throws_whenNotFound() {
        viewModel.loadConstellationsInfo("NOT_EXISTING")
    }


}
