package com.example.skyexplorer

import com.example.skyexplorer.skymapscreen.SkyMapViewModel
import com.example.skyexplorer.skymapscreen.Star
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SkyMapTest{

    @Test
    fun smooth_movesValueTowardsTarget() {
        val vm = SkyMapViewModel(FakeSkyMapRepository())

        val result = vm.smooth(old = 0.0, new = 100.0, alpha = 0.1)

        assertEquals(10.0, result, 0.001)
    }


    @Test
    fun smoothAngle_handlesWrapAround() {
        val vm = SkyMapViewModel(FakeSkyMapRepository())

        val result = vm.smoothAngle(old = 350.0, new = 10.0, alpha = 0.1)

        assertTrue(result in 350.0..360.0 || result in 0.0..20.0)
    }


    @Test
    fun loadStars_setsEmptyList_whenLocationIsNull() = runTest {
        val repo = FakeSkyMapRepository().apply {
            location = null
        }

        val vm = SkyMapViewModel(repo)

        vm.loadStars()
        advanceUntilIdle()

        assertTrue(vm.stars.value.isEmpty())
    }

    @Test
    fun loadStars_setsLoadingTrueThenFalse() = runTest {
        val repo = FakeSkyMapRepository().apply {
            location = null
        }

        val vm = SkyMapViewModel(repo)

        vm.loadStars()
        assertTrue(vm.uiState.value.loading)

        advanceUntilIdle()
        assertFalse(vm.uiState.value.loading)
    }

    @Test
    fun loadConstellations_callsRepository() = runTest {
        val repo = FakeSkyMapRepository()
        val vm = SkyMapViewModel(repo)

        vm.loadConstellations()
        advanceUntilIdle()

        assertTrue(repo.loadConstellationsCalled)
    }

}