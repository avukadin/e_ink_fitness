package com.example.e_inkfitness.core.tools

import com.example.e_inkfitness.core.sensor.AltitudeSample
import kotlin.math.abs

class AltitudeTracker {

    private var smoothedAltitude: Float? = null
    private var lastAcceptedAltitude: AltitudeSample? = null
    private var totalAltitudeGain = 0f

    fun updateAltitude(altitudeSample: AltitudeSample) {
        val altitude = altitudeSample.altitudeMeters

        val previousSmooth = smoothedAltitude
        if (previousSmooth == null) {
            smoothedAltitude = altitudeSample.altitudeMeters
            lastAcceptedAltitude = altitudeSample
            return
        }

        val smooth =
            previousSmooth + SMOOTHING_ALPHA * (altitude - previousSmooth)

        val last = lastAcceptedAltitude!!
        val delta = smooth - last.altitudeMeters
        if (abs(delta) >= MIN_ACCEPTED_CHANGE) {
            if (delta > 0f) {
                totalAltitudeGain += delta
            }
            lastAcceptedAltitude = AltitudeSample(altitudeSample.timestampMs, smooth)
        }
        smoothedAltitude = smooth
    }


    fun getTotalAltitudeGain(): Float = totalAltitudeGain

    fun getAltitude(): AltitudeSample? = lastAcceptedAltitude

    fun reset() {
        smoothedAltitude = null
        lastAcceptedAltitude = null
        totalAltitudeGain = 0f
    }

    companion object {
        private const val SMOOTHING_ALPHA = 0.15f
        private const val MIN_ACCEPTED_CHANGE = 0.5f
    }

}