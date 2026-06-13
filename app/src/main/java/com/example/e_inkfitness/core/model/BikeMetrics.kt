package com.example.e_inkfitness.core.model

// All values in metric
data class BikeMetrics(
    val speed: Float,
    val distance: Float,
    val totalTime: Float,
    val rollingTime: Float,
    val avgRollingSpeed: Float,
    val calories: Float,
    val elevationGain: Float,
)

fun getNewBikeMetrics() : BikeMetrics{
    return BikeMetrics(
        speed = 0f,
        distance = 0f,
        totalTime = 0f,
        rollingTime = 0f,
        avgRollingSpeed = 0f,
        calories = 0f,
        elevationGain = 0f
    )
}