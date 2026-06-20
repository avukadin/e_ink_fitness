package com.example.e_inkfitness.core.tools

import android.location.Location
import com.example.e_inkfitness.core.model.BikeMetrics
import com.example.e_inkfitness.core.model.getNewBikeMetrics
import com.example.e_inkfitness.core.sensor.AltitudeSample
import com.example.e_inkfitness.core.sensor.GpsState

class ActivityTracker(weightKg: Float) {

    var bikeMetrics = BikeMetrics(
        speed = 0f,
        distance = 0f,
        totalTime = 0f,
        rollingTime = 0f,
        avgRollingSpeed = 0f,
        calories = 0f,
        elevationGain = 0f,
    )
        private set

    private var lastLocation: Location? = null
    private val calorieTracker: CalorieTracker = CalorieTracker(weightKg)
    private val altitudeTracker: AltitudeTracker = AltitudeTracker()
    private var lastAltitudeSample: AltitudeSample? = null

    fun recordBikeActivity(location: Location, gptState: GpsState) {

        val altitudeSample = altitudeTracker.getAltitude()
        val prevLocation = lastLocation
        var elapsedTime = 0f
        if (prevLocation != null) {
            elapsedTime = (location.time - prevLocation.time) / 1000f
        }

        val lastAltitude = lastAltitudeSample
        var altitudeDelta = 0f
        if (altitudeSample != null && lastAltitude != null) {
            altitudeDelta = altitudeSample.altitudeMeters - lastAltitude.altitudeMeters
        }

        if (altitudeSample != null) {
            lastAltitudeSample = altitudeSample
        }

        // Only update the time
        if (prevLocation == null || gptState == GpsState.LOW_ACCURACY || !location.hasSpeed() || location.speed <= MIN_MOVING_SPEED) {
            bikeMetrics = bikeMetrics.copy(
                speed = 0f,
                totalTime = elapsedTime + bikeMetrics.totalTime
            )
        } else {

            val distance = prevLocation.distanceTo(location) + bikeMetrics.distance
            val effectiveSpeedMs = if (elapsedTime > GAP_THRESHOLD) {
                prevLocation.distanceTo(location) / elapsedTime
            } else {
                location.speed
            }
            val calories =
                calorieTracker.cyclingCalories(elapsedTime, effectiveSpeedMs, altitudeDelta)
            bikeMetrics = BikeMetrics(
                speed = location.speed,
                distance = distance,
                totalTime = elapsedTime + bikeMetrics.totalTime,
                rollingTime = elapsedTime + bikeMetrics.rollingTime,
                avgRollingSpeed = distance / (elapsedTime + bikeMetrics.rollingTime),
                calories = calories,
                elevationGain = bikeMetrics.elevationGain,
            )
        }
        lastLocation = location
    }

    fun updateAltitude(altitudeSample: AltitudeSample) {
        altitudeTracker.updateAltitude(altitudeSample)
        bikeMetrics = bikeMetrics.copy(elevationGain = altitudeTracker.getTotalAltitudeGain())
    }

    fun clearLastLocation() {
        lastLocation = null
    }

    fun resetAltitudeBaseline() {
        altitudeTracker.resetBaseline()
        lastAltitudeSample = null
    }

    fun reset() {
        bikeMetrics = getNewBikeMetrics()
        calorieTracker.reset()
        altitudeTracker.reset()
        lastAltitudeSample = null
    }

    companion object {
        private val MIN_MOVING_SPEED = UnitConversion.toMS(1.5f)
        private const val GAP_THRESHOLD = 10f // seconds
    }
}