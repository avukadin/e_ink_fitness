package com.example.e_inkfitness.core.tools

import com.example.e_inkfitness.core.model.Units
import kotlin.math.max
import kotlin.math.min


class CalorieTracker(private val weightKg: Float) {

    private var totalCalories = 0f
    private var caloriesToOffset = 0f

    private fun cyclingMet(speedKmh: Float): Float {
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

    fun cyclingCalories(timeSeconds: Float, speedMs: Float, elevationDelta: Float): Float {
        val speedKmh = UnitConversion.convertSpeed(
            speedMs,
            Units.METRIC
        )

        if (elevationDelta != 0.0f) {
            calcElevationCalories(elevationDelta)
        }

        var burnedSince = cyclingMet(speedKmh) * weightKg * (timeSeconds / (60 * 60f))
        var elevationOffset = 0f
        if (caloriesToOffset > 0) {
            elevationOffset = min(burnedSince * 0.5f, caloriesToOffset)
        } else if (caloriesToOffset < 0) {
            elevationOffset = max(-burnedSince * 0.5f, caloriesToOffset)
        }
        burnedSince += elevationOffset
        caloriesToOffset -= elevationOffset

        totalCalories += burnedSince
        return totalCalories
    }

    private fun calcElevationCalories(elevationDelta: Float) {

        var elevationCalories = 0f
        if (elevationDelta > 0) {
            elevationCalories = calcUphillCalories(elevationDelta)
        } else if (elevationDelta < 0) {
            elevationCalories = calcDownhillCalories(elevationDelta)
        }
        caloriesToOffset += elevationCalories

    }

    private fun calcUphillCalories(elevationDelta: Float): Float {
        return weightKg * elevationDelta * UPHILL_KCAL_PER_KG_M
    }

    private fun calcDownhillCalories(elevationDelta: Float): Float {
        return weightKg * elevationDelta * DOWNHILL_KCAL_PER_KG_M
    }

    fun reset() {
        totalCalories = 0f
        caloriesToOffset = 0f
    }

    companion object {
        private const val UPHILL_KCAL_PER_KG_M = 0.0095f
        private const val DOWNHILL_KCAL_PER_KG_M = 0.00234f
    }
}