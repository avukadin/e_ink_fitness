package com.example.e_inkfitness.feature.bike

import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.e_inkfitness.core.model.BikeMetrics
import com.example.e_inkfitness.core.model.Units
import com.example.e_inkfitness.core.model.User
import com.example.e_inkfitness.core.sensor.GpsState
import com.example.e_inkfitness.core.tools.ActivityTracker

class BikeViewModel : ViewModel() {

    private val activityTracker = ActivityTracker()

    var uiState by mutableStateOf(
        BikeUiState(
            metrics = BikeMetrics(
                speed = 0f,
                distance = 0f,
                totalTime = 0f,
                rollingTime = 0f,
                avgRollingSpeed = 0f,
                calories = 0f,
            ),
            user = User(Units.METRIC, 95),
            gpsState = GpsState.WAITING,
            isTracking = false
        )

    )
        private set

    fun onLocation(location: Location, gpsState: GpsState) {
        activityTracker.recordBikeActivity(location, gpsState)

        uiState = uiState.copy(
            metrics = activityTracker.bikeMetrics,
            gpsState = gpsState
        )
    }

    fun onGpsStateChange(gpsState: GpsState) {
        uiState = uiState.copy(
            gpsState = gpsState
        )
    }
}