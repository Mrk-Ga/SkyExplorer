package com.example.skyexplorer

import com.example.skyexplorer.photoGallery.PhotoGalleryViewModel
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi


class PhotoGalleryTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun photoFilter_returnsOnlyMatchingConstellation() {
        val photos = listOf(
            PhotoEntity(uri = "/storage/Orion_123.jpg"),
            PhotoEntity(uri = "/storage/Ursa_Minor_456.jpg"),
            PhotoEntity(uri = "/storage/Orion_789.jpg")
        )

        val viewModel = PhotoGalleryViewModel(
            mockk(relaxed = true),
            FakePhotoRepositoryGalleryTest()
        )

        val result = viewModel.photoFilter(photos, "Orion")

        assertEquals(2, result.size)
        assertTrue(result.all { it.uri.contains("Orion") })
    }

    @Test
    fun photoFilter_returnsEmpty_whenNoMatch() {
        val photos = listOf(
            PhotoEntity(uri = "/storage/Ursa_Minor_456.jpg")
        )

        val viewModel = PhotoGalleryViewModel(
            mockk(relaxed = true),
            FakePhotoRepositoryGalleryTest()
        )

        val result = viewModel.photoFilter(photos, "Orion")

        assertTrue(result.isEmpty())
    }

    @Test
    fun deletePhoto_callsRepository() = runTest {
        val repo = FakePhotoRepositoryGalleryTest()
        val viewModel = PhotoGalleryViewModel(
            mockk(relaxed = true),
            repo
        )

        val photo = PhotoEntity(uri = "file://test.jpg")

        viewModel.deletePhoto(photo)

        advanceUntilIdle()

        assertEquals(1, repo.deleted.size)
        assertEquals(photo, repo.deleted.first())
    }


}
