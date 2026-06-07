package com.example.e_inkfitness.core.tools

import com.example.e_inkfitness.core.model.Units

object UnitConversion {

    fun convertDistance(meters: Float, units: Units): Float {
        return when (units) {
            Units.METRIC -> meters / 1000f      // km
            Units.IMPERIAL -> meters / 1609.344f // miles
        }
    }

    fun convertWeight(kg: Float, units: Units): Float {
        return when (units) {
            Units.METRIC -> kg // kg
            Units.IMPERIAL -> kg * 2.20462f // lbs
        }
    }

    fun convertSpeed(speedMs: Float, units: Units): Float {
        return when (units) {
            Units.METRIC -> speedMs * 3.6f       // km/h
            Units.IMPERIAL -> speedMs * 2.23694f // mph
        }
    }

    fun toMS(kmh: Float): Float {
        return (kmh * 0.277778).toFloat() // m/s
    }
}