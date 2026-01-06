package com.example.skyexplorer

import android.app.Application
import com.example.skyexplorer.photoGallery.PhotoGalleryViewModel
import io.mockk.mockk
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PhotoGalleryViewModelTest {

    private lateinit var viewModel: PhotoGalleryViewModel

    @Before
    fun setUp() {
        val mockApplication = mockk<Application>(relaxed = true)
        viewModel = PhotoGalleryViewModel(mockApplication)
    }

    @Test
    fun `test photoFilter with matching constellation`() {
        val photoList = listOf(
            PhotoEntity(uri = "/path/to/Ursa%20Minor_123.jpg"),
            PhotoEntity(uri = "/path/to/Cassiopeia_456.jpg"),
            PhotoEntity(uri = "/path/to/Ursa%20Minor_789.jpg")
        )

        val filteredList = viewModel.photoFilter(photoList, "Ursa Minor")

        Assert.assertEquals(2, filteredList.size)
        Assert.assertEquals("/path/to/Ursa%20Minor_123.jpg", filteredList[0].uri)
        Assert.assertEquals("/path/to/Ursa%20Minor_789.jpg", filteredList[1].uri)
    }

    @Test
    fun `test photoFilter with no matching constellation`() {
        val photoList = listOf(
            PhotoEntity(uri = "/path/to/Ursa%20Minor_123.jpg"),
            PhotoEntity(uri = "/path/to/Cassiopeia_456.jpg"),
            PhotoEntity(uri = "/path/to/Ursa%20Minor_789.jpg")
        )

        val filteredList = viewModel.photoFilter(photoList, "Orion")

        Assert.assertEquals(0, filteredList.size)
    }

    @Test
    fun `test photoFilter with empty list`() {
        val photoList = emptyList<PhotoEntity>()

        val filteredList = viewModel.photoFilter(photoList, "Ursa Minor")

        Assert.assertEquals(0, filteredList.size)
    }
}