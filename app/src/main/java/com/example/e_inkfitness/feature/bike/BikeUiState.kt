package com.example.e_inkfitness.feature.bike

import com.example.e_inkfitness.core.model.BikeMetrics
import com.example.e_inkfitness.core.model.User
import com.example.e_inkfitness.core.sensor.GpsState

enum class ActivityState {
    PAUSED, ACTIVE, STOPPED
}

data class BikeUiState(
    val metrics: BikeMetrics,
    val user: User,
    val gpsState: GpsState,
    val activityState: ActivityState
)