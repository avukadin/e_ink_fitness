package com.example.e_inkfitness.core.tools



class CalorieTracker (private val weightKg: Float) {

    private var totalCalories = 0f

    fun cyclingMet(speedKmh: Float): Float {
        return when {
            speedKmh < 16f -> 4.0f
            speedKmh < 19f -> 6.8f
            speedKmh < 22f -> 8.0f
            speedKmh < 26f -> 10.0f
            speedKmh < 31f -> 12.0f
            speedKmh < 38f -> 16.0f
            else -> 20.0f
        }
    }

    fun cyclingCalories(timeSeconds:Float, speedKm:Float) : Float{
        val burnedSince =  cyclingMet(speedKm)*weightKg*(timeSeconds/60f)
        totalCalories += burnedSince
        return totalCalories
    }

    fun reset(){
        totalCalories = 0f
    }
}