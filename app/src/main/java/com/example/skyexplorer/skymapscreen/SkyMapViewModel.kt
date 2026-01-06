import android.Manifest
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skyexplorer.skymapscreen.SkyMapRepository
import com.example.skyexplorer.skymapscreen.Constellation
import com.example.skyexplorer.skymapscreen.Star
import com.example.skyexplorer.skymapscreen.raDecToAltAz
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.ZoneOffset
import java.time.ZonedDateTime

data class SkyMapUiState(
    val loading: Boolean,
    val error: Boolean
)

@RequiresApi(Build.VERSION_CODES.O)
class SkyMapViewModel(
    private val repository: SkyMapRepository
) : ViewModel() {

    // ---------- UI STATE ----------

    private val _uiState = MutableStateFlow(SkyMapUiState(false, false))
    val uiState: StateFlow<SkyMapUiState> = _uiState

    private val _stars = MutableStateFlow<List<Star>>(emptyList())
    val stars: StateFlow<List<Star>> = _stars

    private val _constellations = MutableStateFlow<List<Constellation>>(emptyList())
    val constellations: StateFlow<List<Constellation>> = _constellations

    // ---------- STARS LOGIC ----------

    @RequiresPermission(
        allOf = [
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ]
    )
    fun loadStars() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true)

            try {
                val loc = repository.getLocalization()
                if (loc == null) {
                    _stars.value = emptyList()
                    return@launch
                }

                val (lat, lon) = loc
                val timeUtc = ZonedDateTime.now(ZoneOffset.UTC)

                val visibleStars = repository.loadStars()
                    .map { star ->
                        val cords = raDecToAltAz(
                            raDeg = star.ra,
                            decDeg = star.dec,
                            latDeg = lat,
                            lonDeg = lon,
                            dateTime = timeUtc
                        )

                        star.copy(
                            alt = cords.alt,
                            az = cords.az
                        )
                    }
                    .filter { it.magnitude < 6.0 }
                    .sortedByDescending { it.magnitude }

                _stars.value = visibleStars

            } catch (e: Exception) {
                //Log.e("SkyMapViewModel", "loadStars error", e)
            } finally {
                _uiState.value = _uiState.value.copy(loading = false)
            }
        }
    }

    // ---------- CONSTELLATIONS ----------

    fun loadConstellations() /*: List<Constellation>*/{
        viewModelScope.launch {
            try {
                _constellations.value = repository.loadConstellations()
            } catch (e: Exception) {
                //Log.e("SkyMapViewModel", "loadConstellations error", e)
                _constellations.value = emptyList<Constellation>()

            }
        }
    }

    // ---------- SENSOR LOGIC (bez zmian) ----------

    data class ViewDirection(
        val azimuth: Double = 0.0,
        val altitude: Double = 45.0
    )

    var viewDirection by mutableStateOf(ViewDirection())
        private set

    private lateinit var sensorManager: SensorManager
    private var rotationSensor: Sensor? = null

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)
    private val remappedRotationMatrix = FloatArray(9)

    fun startSensors(context: Context) {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        rotationSensor?.let {
            sensorManager.registerListener(
                sensorListener,
                it,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    fun stopSensors() {
        sensorManager.unregisterListener(sensorListener)
    }

    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type != Sensor.TYPE_ROTATION_VECTOR) return

            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

            val success = SensorManager.remapCoordinateSystem(
                rotationMatrix,
                SensorManager.AXIS_X,
                SensorManager.AXIS_Z,
                remappedRotationMatrix
            )

            if (!success) return

            SensorManager.getOrientation(remappedRotationMatrix, orientationAngles)

            val azimuthDeg =
                (Math.toDegrees(orientationAngles[0].toDouble()) + 360) % 360
            val pitchDeg = Math.toDegrees(orientationAngles[1].toDouble())

            viewDirection = ViewDirection(
                azimuth = smoothAngle(viewDirection.azimuth, azimuthDeg),
                altitude = smooth(viewDirection.altitude, pitchDeg)
            )
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    // ---------- MATH (TESTOWALNE) ----------

    internal fun smooth(old: Double, new: Double, alpha: Double = 0.1): Double =
        old + alpha * (new - old)

    internal fun smoothAngle(old: Double, new: Double, alpha: Double = 0.1): Double {
        val diff = (new - old + 540) % 360 - 180
        return (old + alpha * diff + 360) % 360
    }

    val fieldOfView = 70.0
}

