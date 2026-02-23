package com.example.skyexplorer

import android.app.Application
import com.example.skyexplorer.camera.CameraViewModel
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import kotlin.test.Test
import kotlin.test.assertEquals

//@OptIn(ExperimentalCoroutinesApi::class)
class CameraViewModelTest {

    private lateinit var repo: FakePhotoRepositoryCameraTest
    private lateinit var viewModel: CameraViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()


    @Before
    fun setup() {
        repo = FakePhotoRepositoryCameraTest()
        val app = mockk<Application>(relaxed = true)
        viewModel = CameraViewModel(repo, app)
    }

    @Test
    fun savePhotoString_insertsPhotoIntoRepository() = runTest {
        viewModel.savePhotoString("file://photo.jpg")

        advanceUntilIdle()

        assertEquals(1, repo.insertedUris.size)
        assertEquals("file://photo.jpg", repo.insertedUris.first())
    }
}


@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

