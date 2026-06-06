package com.example.e_inkfitness.core.tools

import com.example.e_inkfitness.core.model.Units

object UnitConversion{

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

    fun convertSpeed(kmh: Float, units: Units): Float {
        return when (units) {
            Units.METRIC -> kmh // km/h
            Units.IMPERIAL -> kmh * 0.621371f // mp/h
        }
    }

    fun toMS(kmh:Float):Float{
        return (kmh*0.277778).toFloat() // m/s
    }
}