package com.example.e_inkfitness.core.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

data class AltitudeSample(
    val timestampMs: Long,
    val altitudeMeters: Float
)

interface AltitudeCallback {
    fun onAltitude(altitudeSample: AltitudeSample)
}

class AltitudeProvider(
    context: Context,
    private val callback: AltitudeCallback
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)

    fun start() {
        pressureSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_PRESSURE) return

        val now = System.currentTimeMillis()
        val pressureHPa = event.values[0]

        val altitudeMeters = SensorManager.getAltitude(
            SensorManager.PRESSURE_STANDARD_ATMOSPHERE,
            pressureHPa
        )
        callback.onAltitude(AltitudeSample(now, altitudeMeters))
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
