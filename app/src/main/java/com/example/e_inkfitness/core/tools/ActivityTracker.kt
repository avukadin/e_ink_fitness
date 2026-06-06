package com.example.e_inkfitness.core.tools

import android.location.Location
import com.example.e_inkfitness.core.model.BikeMetrics

class ActivityTracker {

    var  bikeMetrics = BikeMetrics(
        speed = 0f,
        distance = 0f,
        totalTime = 0f,
        rollingTime = 0f,
        avgRollingSpeed = 0f,
        calories = 0f,
    )
    private set

    private var lastLocation: Location? = null

    fun recordBikeActivity(location: Location){
        if (!location.hasSpeed()) return
        if (location.hasAccuracy() && location.accuracy > 25f) return

        val speed = location.speed

        val prevLocation = lastLocation
        if (prevLocation != null ){
            val lastTime = prevLocation.time

            val elapsedTime = (location.time - lastTime) / 1000f
            val distance = prevLocation.distanceTo(location)

            var rollingTime = 0f
            if (speed >= MIN_MOVING_SPEED){
                rollingTime = (location.time - lastTime) / 1000f
            }
            bikeMetrics = BikeMetrics(
                speed=speed,
                distance=distance,
                totalTime = elapsedTime + bikeMetrics.totalTime,
                rollingTime = rollingTime + bikeMetrics.rollingTime,
                avgRollingSpeed = distance/(rollingTime + bikeMetrics.rollingTime),
                calories = 0f,
            )
        }
        lastLocation = location
    }

    companion object {
        private val MIN_MOVING_SPEED = UnitConversion.toMS(1.5f)
    }
}