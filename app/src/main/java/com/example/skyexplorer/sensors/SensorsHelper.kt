package com.example.skyexplorer.sensors
/*
import SkyMapViewModel
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin


data class ViewDirection(
    val azimuth: Double = 0.0,   // 0–360
    val altitude: Double = 45.0  // 0–90
)
/*
var viewDirection by mutableStateOf(ViewDirection())
    private set
*/
private lateinit var sensorManager: SensorManager
private var rotationSensor: Sensor? = null

private val rotationMatrix = FloatArray(9)
private val orientationAngles = FloatArray(3)

class SensorsHeper(viewModel: SkyMapViewModel){

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

    private val remappedRotationMatrix = FloatArray(9)

    private val sensorListener = object : SensorEventListener {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

                // --- KLUCZOWA ZMIANA: Przemapowanie układu współrzędnych ---
                // Zmieniamy układ z "telefon na stole" na "telefon jako okno (AR)"
                // AXIS_X i AXIS_Z mapują układ tak, że telefon trzymany pionowo działa poprawnie.
                val success = SensorManager.remapCoordinateSystem(
                    rotationMatrix,
                    SensorManager.AXIS_X,
                    SensorManager.AXIS_Z,
                    remappedRotationMatrix
                )

                if (success) {
                    SensorManager.getOrientation(remappedRotationMatrix, orientationAngles)

                    // orientationAngles[0] -> Azymut (rad)
                    // orientationAngles[1] -> Pitch (rad) - tutaj to będzie nasza wysokość (Altitude)
                    // orientationAngles[2] -> Roll (rad)

                    val azimuthRad = orientationAngles[0]
                    val pitchRad = orientationAngles[1]

                    // Konwersja na stopnie
                    var azimuthDeg = Math.toDegrees(azimuthRad.toDouble())
                    // Normalizacja azymutu do 0-360
                    if (azimuthDeg < 0) azimuthDeg += 360.0

                    val pitchDeg = Math.toDegrees(pitchRad.toDouble())

                    // W tym układzie (po remap):
                    // pitchDeg = 0 -> horyzont
                    // pitchDeg = 90 -> patrzenie w dół (lub górę zależnie od implementacji remap)
                    // Zazwyczaj przy AXIS_X, AXIS_Z patrzenie w górę daje wartości ujemne lub dodatnie zależnie od definicji.
                    // Dla konfiguracji X/Z: Horyzont ~0, Zenith (w górę) ~90.

                    // Aktualizacja widoku
                    viewModel.updateViewDiraction( ViewDirection(
                        azimuth = azimuthDeg,
                            // smoothAngle(viewDirection.azimuth, azimuthDeg),
                        // Altitude w tym mapowaniu to zazwyczaj pitch.
                        // Czasami trzeba dodać offset, ale przy remap X/Z powinno być wprost.
                        altitude = pitchDeg
                        ///smooth(viewDirection.altitude, pitchDeg)
                        )
                    )

                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private fun smooth(old: Double, new: Double, alpha: Double = 0.1): Double {
        return old + alpha * (new - old)
    }

    private fun smoothAngle(old: Double, new: Double, alpha: Double = 0.1): Double {
        var diff = (new - old + 540) % 360 - 180
        return (old + alpha * diff + 360) % 360
    }

    var fieldOfView = 70.0


}

fun angularDistance(
    az1: Double, alt1: Double,
    az2: Double, alt2: Double
): Double {
    val a1 = Math.toRadians(az1)
    val a2 = Math.toRadians(az2)
    val z1 = Math.toRadians(alt1)
    val z2 = Math.toRadians(alt2)

    return Math.toDegrees(
        acos(
            sin(z1) * sin(z2) +
                    cos(z1) * cos(z2) * cos(a1 - a2)
        )
    )
}

 */
