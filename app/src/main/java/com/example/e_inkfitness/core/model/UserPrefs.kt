package com.example.e_inkfitness.core.model

enum class Units {
    METRIC, IMPERIAL
}

data class User(
    val units: Units,
    val weightKG: Int,
)