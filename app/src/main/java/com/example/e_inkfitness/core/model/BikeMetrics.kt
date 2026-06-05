package com.example.e_inkfitness.core.model

data class BikeMetrics(
    val speed: Float,
    val distanceKM: Float,
    val rideTimeSeconds: Float,
    val rollingTimeSeconds: Float,
    val avgRollingSpeedKMH: Float,
    val caloriesBurned: Float,
)