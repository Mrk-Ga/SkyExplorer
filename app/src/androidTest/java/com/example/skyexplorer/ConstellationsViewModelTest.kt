package com.example.skyexplorer

import androidx.test.core.app.ApplicationProvider
import com.example.skyexplorer.constellations.ConstellationsViewModel
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertTrue
import android.app.Application
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.skyexplorer.data.constellationInfoFilename
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConstellationsViewModelInstrumentedTest {

    private lateinit var viewModel: ConstellationsViewModel

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        viewModel = ConstellationsViewModel(context)
    }

    @Test
    fun loadConstellationsInfo_returnsNonEmptyList() {
        val result = viewModel.loadConstellationsInfo()

        assertTrue(result.isNotEmpty())
    }

    @Test
    fun constellationInfoFile_existsInAssets() {
        val context = ApplicationProvider.getApplicationContext<Application>()

        val files = context.assets.list("") ?: emptyArray()

        assertTrue(files.contains(constellationInfoFilename))
    }
}

