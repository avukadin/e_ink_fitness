package com.example.e_inkfitness.core.tools

import android.location.Location
import com.example.e_inkfitness.core.model.BikeMetrics
import com.example.e_inkfitness.core.sensor.GpsState

class ActivityTracker(weightKg: Float) {

    var bikeMetrics = BikeMetrics(
        speed = 0f,
        distance = 0f,
        totalTime = 0f,
        rollingTime = 0f,
        avgRollingSpeed = 0f,
        calories = 0f,
    )
        private set

    private var lastLocation: Location? = null
    private val calorieTracker: CalorieTracker = CalorieTracker(weightKg)

    fun recordBikeActivity(location: Location, gpsState: GpsState) {
        val prevLocation = lastLocation
        var elapsedTime = 0f
        if (prevLocation != null) {
            elapsedTime = (location.time - prevLocation.time) / 1000f
        }

        // Only update the time
        if (prevLocation == null || gpsState == GpsState.LOW_ACCURACY || !location.hasSpeed() || location.speed <= MIN_MOVING_SPEED) {
            bikeMetrics = bikeMetrics.copy(
                speed = 0f,
                totalTime = elapsedTime + bikeMetrics.totalTime
            )
        } else {
            val distance = prevLocation.distanceTo(location) + bikeMetrics.distance
            bikeMetrics = BikeMetrics(
                speed = location.speed,
                distance = distance,
                totalTime = elapsedTime + bikeMetrics.totalTime,
                rollingTime = elapsedTime + bikeMetrics.rollingTime,
                avgRollingSpeed = distance / (elapsedTime + bikeMetrics.rollingTime),
                calories = calorieTracker.cyclingCalories(elapsedTime, location.speed),
            )
        }

        lastLocation = location
    }

    fun clearLastLocation(){
        lastLocation = null
    }

    fun reset(){
        bikeMetrics = BikeMetrics(
            speed = 0f,
            distance = 0f,
            totalTime = 0f,
            rollingTime = 0f,
            avgRollingSpeed = 0f,
            calories = 0f,
        )
        calorieTracker.reset()
    }

    companion object {
        private val MIN_MOVING_SPEED =
            UnitConversion.toMS(1.5f)
    }
}