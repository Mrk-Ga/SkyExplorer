import android.Manifest
import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.example.skyexplorer.skymapscreen.HorizontalCoordinates
import com.example.skyexplorer.skymapscreen.SkyMapIntent
import com.example.skyexplorer.skymapscreen.SkyMapModel
import com.example.skyexplorer.skymapscreen.SkyMapUiState
import com.example.skyexplorer.skymapscreen.Star
import com.example.skyexplorer.skymapscreen.raDecToAltAz
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.time.ZoneOffset
import java.time.ZonedDateTime

@RequiresApi(Build.VERSION_CODES.O)
data class SkyUiState  constructor(
    val stars: List<Star> = emptyList(),
    //val constellations: List<Constellation> = emptyList(),
    val lat: Double? = null,
    val lon: Double? = null,
    val timeUtc: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC),
    val loading: Boolean = true,
    val error: String? = null
)


class SkyMapViewModel (application: Application): AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(SkyMapUiState())
    val uiState: StateFlow<SkyMapUiState> = _uiState

    private val model= SkyMapModel()

    private val _stars = MutableStateFlow<List<Star>>(emptyList())
    val stars: StateFlow<List<Star>> = _stars


    @RequiresApi(Build.VERSION_CODES.O)
    fun handleIntent(intent: SkyMapIntent){
        when (intent) {

            is SkyMapIntent.RequestNavigationPermission -> {
                _uiState.value = _uiState.value.copy(hasPermission = true)
            }

            is SkyMapIntent.NavigateToCamera -> {SkyMapIntent.NavigateToCamera}
            is SkyMapIntent.NavigateToConstellations -> {SkyMapIntent.NavigateToConstellations}

        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun getTime(): ZonedDateTime {
        return ZonedDateTime.now(ZoneOffset.UTC)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION])
    suspend fun createVisibleStars(): List<Star>{
        val context = getApplication<Application>().applicationContext

        val loc = model.getLocalizationSuspend(application = application)
        if(loc == null){
            return emptyList()
        }
        val starsJsonString = context.assets.open("stars.json")
            .bufferedReader()
            .use { it.readText() }
            .replace("NaN", "null") // Dodaj tę linię, aby zamienić NaN na null

        val starsDecoded = Json.decodeFromString<List<Star>>(starsJsonString)
        //Log.d("STARS", starsDecoded.toString())


        val stars = mutableListOf<Star>()

        starsDecoded.forEach { star ->
            val cords: HorizontalCoordinates
            val timeUtc = getTime()
            cords = raDecToAltAz(star.ra*15.0, star.dec, loc.first, loc.second, timeUtc)
            star.alt = cords.alt
            star.az = cords.az

            if (star.alt!! > 0 && star.magnitude < 6.0) {
                stars.add(star)
            }
        }



        //Log.d("STARS", stars.toString())

        return stars.sortedBy{star -> star.magnitude}.take(2000)




    }

    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun loadStars() {
        Log.d("DEBUG", "Wywołano loadStars()")

        viewModelScope.launch {
            Log.d("DEBUG", "Rozpoczynam createVisibleStars() w korutynie")

            val result = createVisibleStars()
            Log.d("DEBUG", "Załadowano ${result.size} widocznych gwiazd")

            _stars.value = result
        }
    }



}