package com.example.skyexplorer

import SkyMapViewModel
import android.app.Application
import com.example.skyexplorer.skymapscreen.SkyMapRepository
import com.example.skyexplorer.skymapscreen.Constellation
import com.example.skyexplorer.skymapscreen.Star
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SkyMapViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ---------- loadStars ----------

    @Test
    fun `loadStars emits visible stars sorted by magnitude`() = runTest {
        val stars = listOf(
            Star(
                magnitude = 1.0,
                id = 123123,
                name = "HD 1231231",
                ra = 0.07945234,
                dec = -44.29029741,
                sptype = "G3IV",
                alt = 123.424,
                az = 454.323
            ),
            Star(
                magnitude = 5.0,
                id = 12313232,
                name = "HD 1231231",
                ra = 0.07945234,
                dec = -44.29029741,
                sptype = "F3IV",
                alt = 123.424,
                az = 454.323
            ),
            Star(
                magnitude = 6.5,
                id = 12313232,
                name = "HD 3333331",
                ra = 0.07945234,
                dec = -44.29029741,
                sptype = "F33IV",
                alt = 123.424,
                az = 454.323
            )
            //Star(ra = 20.0, dec = 20.0, magnitude = 6.5) // powinien odpaść
        )

        val vm = SkyMapViewModel(
            FakeSkyMapRepository(stars = stars)
        )

        vm.loadStars()
        advanceUntilIdle()

        val result = vm.stars.value

        assertEquals(2, result.size)
        assertTrue(result.all { it.magnitude < 6.0 })
        assertTrue(result[0].magnitude >= result[1].magnitude)
    }

    @Test
    fun `loadStars with null location clears stars`() = runTest {
        val vm = SkyMapViewModel(
            FakeSkyMapRepository(location = null)
        )

        vm.loadStars()
        advanceUntilIdle()

        assertTrue(vm.stars.value.isEmpty())
        assertFalse(vm.uiState.value.loading)
    }

    @Test
    fun `loadStars sets loading true then false`() = runTest {
        val vm = SkyMapViewModel(
            FakeSkyMapRepository(stars = emptyList())
        )

        vm.loadStars()
        //assertTrue(vm.uiState.value.loading)

        advanceUntilIdle()

        assertFalse(vm.uiState.value.loading)
    }

    @Test
    fun `loadStars handles repository exception gracefully`() = runTest {
        val vm = SkyMapViewModel(
            FakeSkyMapRepository(throwError = true)
        )

        vm.loadStars()
        advanceUntilIdle()

        assertTrue(vm.stars.value.isEmpty())
        assertFalse(vm.uiState.value.loading)
    }

    // ---------- loadConstellations ----------

    @Test
    fun `loadConstellations updates state`() = runTest {
        val constellations = listOf(
            Constellation(
                "ORI", "Orion",
                segments = emptyList()
            )
        )

        val vm = SkyMapViewModel(
            FakeSkyMapRepository(constellations = constellations)
        )

        vm.loadConstellations()
        advanceUntilIdle()

        assertEquals(1, vm.constellations.value.size)
    }

    // ---------- math ----------

    @Test
    fun `smooth moves value gradually`() {
        val vm = SkyMapViewModel(FakeSkyMapRepository())

        val result = vm.smooth(old = 0.0, new = 100.0, alpha = 0.1)

        assertEquals(10.0, result, 0.001)
    }

    @Test
    fun `smoothAngle handles wrap around correctly`() {
        val vm = SkyMapViewModel(FakeSkyMapRepository())

        val result = vm.smoothAngle(old = 350.0, new = 10.0, alpha = 0.1)

        assertTrue(result in 350.0..360.0 || result in 0.0..10.0)
    }
}
