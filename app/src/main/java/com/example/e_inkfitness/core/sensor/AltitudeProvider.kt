package com.example.e_inkfitness.core.sensor

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

data class AltitudeSample(
    val timestampMs: Long,
    val altitudeMeters: Float
)


class AltitudeProvider : SensorEventListener {

    var altitudeSample : AltitudeSample? = null

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_PRESSURE) return

        val now = System.currentTimeMillis()
        val pressureHPa = event.values[0]

        val altitudeMeters = SensorManager.getAltitude(
            SensorManager.PRESSURE_STANDARD_ATMOSPHERE,
            pressureHPa
        )
        altitudeSample = AltitudeSample(now, altitudeMeters)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}