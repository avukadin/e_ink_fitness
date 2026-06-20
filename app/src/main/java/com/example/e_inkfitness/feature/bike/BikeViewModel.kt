package com.example.e_inkfitness.feature.bike

import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.e_inkfitness.core.model.Units
import com.example.e_inkfitness.core.model.User
import com.example.e_inkfitness.core.model.getNewBikeMetrics
import com.example.e_inkfitness.core.sensor.AltitudeSample
import com.example.e_inkfitness.core.sensor.GpsState
import com.example.e_inkfitness.core.tools.ActivityTracker

class BikeViewModel : ViewModel() {

    private val defaultUser = User(Units.METRIC, 95.0f)
    private val activityTracker = ActivityTracker(defaultUser.weightKG)

    var uiState by mutableStateOf(
        BikeUiState(
            metrics = getNewBikeMetrics(),
            user = defaultUser,
            gpsState = GpsState.STOPPED,
            activityState = ActivityState.STOPPED
        )

    )
        private set

    fun onLocation(location: Location, gpsState: GpsState) {
        if (uiState.activityState != ActivityState.ACTIVE) {
            return
        }
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
        if (uiState.gpsState == GpsState.DISABLED) {
            activityTracker.clearLastLocation()
        }
    }

    fun onAltitudeChange(altitudeSample: AltitudeSample) {
        activityTracker.updateAltitude(altitudeSample)
    }

    fun onPauseClicked() {
        val metrics = uiState.metrics.copy(speed = 0f)
        uiState = uiState.copy(
            activityState = ActivityState.PAUSED,
            metrics = metrics
        )
        onGpsStateChange(GpsState.STOPPED)
        activityTracker.clearLastLocation()
    }

    fun onStopClicked() {
        uiState = uiState.copy(
            activityState = ActivityState.STOPPED,
            metrics = getNewBikeMetrics()
        )
        activityTracker.clearLastLocation()
        activityTracker.reset()
    }

    fun onResumeClicked() {
        uiState = uiState.copy(
            activityState = ActivityState.ACTIVE,
            gpsState = GpsState.WAITING
        )
    }
}
