import android.Manifest
import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.example.skyexplorer.skymapscreen.Constellation
import com.example.skyexplorer.skymapscreen.SkyMapIntent
import com.example.skyexplorer.skymapscreen.SkyMapModel
import com.example.skyexplorer.skymapscreen.SkyMapUiState
import com.example.skyexplorer.skymapscreen.Star
import com.example.skyexplorer.skymapscreen.raDecToAltAz
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.time.ZoneOffset
import java.time.ZonedDateTime

@RequiresApi(Build.VERSION_CODES.O)
data class SkyUiState (
    val stars: List<Star> = emptyList(),
    val constellations: List<Constellation> = emptyList(),
    val lat: Double? = null,
    val lon: Double? = null,
    val timeUtc: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC),
    val loading: Boolean = true,
    val error: String? = null
)


@RequiresApi(Build.VERSION_CODES.O)
class SkyMapViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(SkyMapUiState(false, false))
    val uiState: StateFlow<SkyMapUiState> = _uiState

    private val model = SkyMapModel()

    private val _stars = MutableStateFlow<List<Star>>(emptyList())
    private val _constellations = MutableStateFlow<List<Constellation>>(emptyList())
    val stars: StateFlow<List<Star>> = _stars
    val constellations: StateFlow<List<Constellation>> = _constellations

    // Cache dla surowych danych z JSON, żeby nie czytać pliku w kółko
    private var allStarsCache: List<Star>? = null

    @RequiresApi(Build.VERSION_CODES.O)
    fun handleIntent(intent: SkyMapIntent) {
        when (intent) {
            is SkyMapIntent.RequestNavigationPermission -> {
                _uiState.value = _uiState.value.copy(hasPermission = true,)
            }
            // Pamiętaj, aby obsłużyć te intencje lub usunąć puste bloki, jeśli nic nie robią
            is SkyMapIntent.NavigateToCamera -> { /* Logika nawigacji */ }
            is SkyMapIntent.NavigateToConstellations -> { /* Logika nawigacji */ }
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun getTime(): ZonedDateTime {
        return ZonedDateTime.now(ZoneOffset.UTC)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    suspend fun createVisibleStars(): List<Star> = withContext(Dispatchers.Default) {
        // 1. Pobierz lokalizację
        val loc = model.getLocalizationSuspend(application = application)
        if (loc == null) {
            Log.e("SkyMapViewModel", "Brak lokalizacji")
            return@withContext emptyList()
        }

        // 2. Wczytaj JSON tylko raz (Cache)
        if (allStarsCache == null) {
            try {
                val context = getApplication<Application>().applicationContext
                val starsJsonString = context.assets.open("stars.json")
                    .bufferedReader()
                    .use { it.readText() }
                    .replace("NaN", "null")

                // JSON decode może być kosztowny, robimy to raz
                allStarsCache = Json.decodeFromString<List<Star>>(starsJsonString)
            } catch (e: Exception) {
                Log.e("SkyMapViewModel", "Błąd wczytywania JSON: ${e.message}")
                return@withContext emptyList()
            }
        }

        // Używamy bezpiecznie odpakowanej listy
        val sourceStars = allStarsCache ?: emptyList()
        val calculatedStars = mutableListOf<Star>()

        // 3. Pobierz czas RAZ przed pętlą
        val timeUtc = getTime()

        // 4. Obliczenia
        sourceStars.forEach { star ->
            // Kopiujemy obiekt gwiazdy, żeby nie modyfikować cache (ważne przy wielokrotnym odświeżaniu!)
            // Jeśli Star jest 'data class', użyj copy(). Jeśli nie, musisz stworzyć nową instancję.
            // Zakładam, że Star to data class.
            val currentStar = star.copy()

            // --- KLUCZOWA POPRAWKA: USUNIĘTO * 15.0 ---
            // Twoje RA w JSON jest w stopniach. Funkcja matematyczna też oczekuje stopni.
            val cords = raDecToAltAz(
                raDeg = currentStar.ra,
                decDeg = currentStar.dec,
                latDeg = loc.first,
                lonDeg = loc.second,
                dateTime = timeUtc
            )

            currentStar.alt = cords.alt
            currentStar.az = cords.az

            // Filtrowanie - opcjonalnie możesz zwiększyć limit, jeśli dodałeś obsługę rozmiarów
            // Jeśli magnitude < 6.0, zobaczymy więcej gwiazd (bliżej rzeczywistości)
            if (currentStar.magnitude < 6.0) {
                calculatedStars.add(currentStar)
            }
        }

        // Sortowanie, żeby najpierw rysować ciemne, a na wierzchu jasne gwiazdy (opcjonalne)
        return@withContext calculatedStars.sortedByDescending { it.magnitude }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun loadStars() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true)

            try {
                val result = createVisibleStars()
                _stars.value = result
            } catch (e: SecurityException) {
                Log.e("SkyMapViewModel", "Brak uprawnień lokalizacji")
            } catch (e: Exception) {
                Log.e("SkyMapViewModel", "Błąd generowania gwiazd: ${e.message}")
            } finally {
                _uiState.value = _uiState.value.copy(loading = false)
            }
        }
    }

    // Ładowanie konstelacji też warto przenieść na IO
    suspend fun loadConstellations(): List<Constellation> = withContext(Dispatchers.IO) {
        try {
            val context = getApplication<Application>().applicationContext
            // Upewnij się, że nazwa pliku jest poprawna!
            val json = context.assets.open("constellations_lines.json")
                .bufferedReader()
                .use { it.readText() }

            val result = Json.decodeFromString<List<Constellation>>(json)
            Log.d("CONST_DEBUG", "Załadowano pomyślnie: ${result.size} konstelacji")
            _constellations.value = result
            return@withContext result

        } catch (e: Exception) {
            // TO JEST KLUCZOWE: Zobaczysz, dlaczego nie działa
            Log.e("CONST_DEBUG", "Błąd wczytywania konstelacji: ${e.message}")
            e.printStackTrace()
            return@withContext emptyList()
        }
    }
}