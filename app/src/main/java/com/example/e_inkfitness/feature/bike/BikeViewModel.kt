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

    private val activityTracker = ActivityTracker(95f)

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
            activityState = ActivityState.ACTIVE
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
        if (uiState.gpsState == GpsState.DISABLED){
            activityTracker.clearLastLocation()
        }
    }

    fun onPauseClicked(){
        uiState = uiState.copy(
            activityState = ActivityState.PAUSED
        )
        onGpsStateChange(GpsState.STOPPED)
        activityTracker.clearLastLocation()
    }

    fun onStopClicked(){
        uiState = uiState.copy(
            activityState = ActivityState.STOPPED,
            metrics = BikeMetrics(
                speed = 0f,
                distance = 0f,
                totalTime = 0f,
                rollingTime = 0f,
                avgRollingSpeed = 0f,
                calories = 0f,
            ),
        )
        activityTracker.clearLastLocation()
        activityTracker.reset()
    }

    fun onResumeClicked(){
        uiState = uiState.copy(
            activityState = ActivityState.ACTIVE
        )
    }
}