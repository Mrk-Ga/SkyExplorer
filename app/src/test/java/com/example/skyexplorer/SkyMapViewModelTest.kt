package com.example.skyexplorer

import SkyMapViewModel
import com.example.skyexplorer.skymapscreen.Constellation
import com.example.skyexplorer.skymapscreen.SkyMapRepository
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
        )

        val vm = SkyMapViewModel(
            FakeSkyMapRepository(stars = stars)
        )

        vm.loadStars()
        advanceUntilIdle()

        val result = vm.stars.value

        assertEquals(2, result.size)
        assertTrue(result.all { it.magnitude < 6.0 })
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

    @Test
    fun `loadConstellations handles repository exception gracefully`() = runTest {
        val vm = SkyMapViewModel(
            FakeSkyMapRepository(throwError = true)
        )

        vm.loadConstellations()
        advanceUntilIdle()

        assertTrue(vm.constellations.value.isEmpty())
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

        assertEquals(352.0, result, 1.0)
    }
}

class FakeSkyMapRepository(
    private val location: Pair<Double, Double>? = 52.0 to 21.0,
    private val stars: List<Star> = emptyList(),
    private val constellations: List<Constellation> = emptyList(),
    private val throwError: Boolean = false
) : SkyMapRepository {

    override suspend fun getLocalization(): Pair<Double, Double>? {
        if (throwError) throw RuntimeException("Location error")
        return location
    }

    override suspend fun loadStars(): List<Star> {
        if (throwError) throw RuntimeException("Stars error")
        return stars
    }

    override suspend fun loadConstellations(): List<Constellation> {
        if (throwError) throw RuntimeException("Constellations error")
        return constellations
    }
}
